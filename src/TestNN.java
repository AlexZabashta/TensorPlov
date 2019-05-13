import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;

import clsf.Dataset;
import core.Result;
import dataset.Convolution;
import dataset.FullConvolution;
import dataset.FullConvolution.Input;
import dataset.SymConvolution;
import mfextraction.CMFExtractor;
import mfextraction.KNNLandMark;

public class TestNN {
    public static void main(String[] args) throws IOException {

        final List<Dataset> datasets = new ArrayList<>();

        final int numFeatures = 16;
        final int numObjectsPerClass = 64;

        String[] classNames = { "zero", "one" };

        final int numClasses = classNames.length;
        final int numObjects = numObjectsPerClass * numClasses;

        for (File datafolder : new File("csv").listFiles()) {
            try {
                double[][] data = new double[numObjects][numFeatures];
                int[] labels = new int[numObjects];

                String[] header = new String[numFeatures];

                for (int f = 0; f < numFeatures; f++) {
                    header[f] = "f" + f;
                }

                for (int oid = 0, label = 0; label < numClasses; label++) {
                    try (CSVParser parser = new CSVParser(new FileReader(datafolder.getPath() + File.separator + classNames[label] + ".csv"), CSVFormat.DEFAULT.withHeader(header))) {
                        for (CSVRecord record : parser) {
                            for (int fid = 0; fid < numFeatures; fid++) {
                                data[oid][fid] = Double.parseDouble(record.get(fid));
                            }
                            labels[oid++] = label;
                        }
                    }
                }

                datasets.add(new Dataset(datafolder.getName(), true, data, false, labels));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        final int numData = datasets.size();
        System.out.println(numData);
        final double[][] metaData = new double[numData][];
        HVFold hvFold = new HVFold();
        Convolution convolution = new SymConvolution(40, hvFold, hvFold);

        Encoder encoder = new Encoder();
        Decoder decoder = new Decoder();
        Simple simple = new Simple();

        FullConvolution net = new FullConvolution(2, encoder, convolution, decoder, 1);

        double[] enc = new double[encoder.numBoundVars()];
        double[] dec = new double[decoder.numBoundVars()];
        double[] hor = new double[hvFold.numBoundVars()];
        double[] ver = new double[hvFold.numBoundVars()];
        double[] sim = new double[simple.numBoundVars()];
        encoder.init(enc);
        decoder.init(dec);
        hvFold.init(hor);
        hvFold.init(ver);
        simple.init(sim);

        double[] menc = new double[enc.length];
        double[] venc = new double[enc.length];
        Arrays.fill(venc, 1.0);
        double[] mhor = new double[hor.length];
        double[] vhor = new double[hor.length];
        Arrays.fill(vhor, 1.0);
        double[] mver = new double[ver.length];
        double[] vver = new double[ver.length];
        Arrays.fill(vver, 1.0);
        double[] mdec = new double[dec.length];
        double[] vdec = new double[dec.length];
        Arrays.fill(vdec, 1.0);
        double[] msim = new double[sim.length];
        double[] vsim = new double[sim.length];
        Arrays.fill(vsim, 1.0);

        int batchs = 20;
        double lr = 10;
        double alpha = 0.97;
        double beta1 = 0.95;
        double beta2 = 0.95;

        double mseS = 1;
        double mseC = 1;

        CMFExtractor extractor = new CMFExtractor();
        int numMF = extractor.length();

        for (int i = 0; i < numData; i++) {
            metaData[i] = extractor.apply(datasets.get(i));
        }

        final double[] min = new double[numMF];
        final double[] max = new double[numMF];

        Arrays.fill(min, Double.POSITIVE_INFINITY);
        Arrays.fill(max, Double.NEGATIVE_INFINITY);

        for (int i = 0; i < numData; i++) {
            for (int j = 0; j < numMF; j++) {
                min[j] = Math.min(min[j], metaData[i][j]);
                max[j] = Math.max(max[j], metaData[i][j]);
            }
        }
        ToDoubleFunction<Dataset> knnScore = new KNNLandMark();

        Random random = new Random();
        for (int iter = 0; iter < 100000; iter++) {
            for (int batch = 0; batch < batchs; batch++) {
                Dataset dataset = datasets.get(random.nextInt(numData));
                double[] rmf = extractor.apply(dataset);
                double[] nmf = new double[numMF];

                for (int i = 0; i < numMF; i++) {
                    nmf[i] = (rmf[i] - min[i]) / (max[i] - min[i]) * 2 - 1;
                }

                Result<Pair<double[], double[]>, double[]> sp = simple.result(nmf, sim);

                double[] ys = sp.value();
                double ty = knnScore.applyAsDouble(dataset) * 2 - 1;
                double diffs = ys[0] - ty;
                double[] dys = { diffs };

                double[] deltas = sp.derivative().apply(dys).getRight();

                for (int i = 0; i < sim.length; i++) {
                    msim[i] = beta1 * msim[i] + (1 - beta1) * deltas[i];
                    vsim[i] = beta2 * vsim[i] + (1 - beta2) * deltas[i] * deltas[i];
                    sim[i] -= lr * msim[i] / (Math.sqrt(vsim[i]) + 1e-8);
                }
                mseS = alpha * mseS + (1 - alpha) * diffs * diffs;

                double[][][] obj = new double[numObjects][numFeatures][2];

                for (int oid = 0; oid < numObjects; oid++) {
                    for (int fid = 0; fid < numFeatures; fid++) {
                        obj[oid][fid][0] = dataset.data[oid][fid];
                        obj[oid][fid][1] = dataset.labels[oid];
                    }
                }

                Result<Input, double[]> cp = net.result(obj, enc, hor, ver, dec);

                double[] yc = cp.value();
                double diffc = yc[0] - ty;

                mseC = alpha * mseC + (1 - alpha) * diffc * diffc;

                double[] dyc = { diffc };

                FullConvolution.Input delta = cp.derivative().apply(dyc);
                for (int i = 0; i < enc.length; i++) {
                    menc[i] = beta1 * menc[i] + (1 - beta1) * delta.enc[i];
                    venc[i] = beta2 * venc[i] + (1 - beta2) * delta.enc[i] * delta.enc[i];
                    enc[i] -= lr * menc[i] / (Math.sqrt(venc[i]) + 1e-8);
                }
                for (int i = 0; i < dec.length; i++) {
                    mdec[i] = beta1 * mdec[i] + (1 - beta1) * delta.dec[i];
                    vdec[i] = beta2 * vdec[i] + (1 - beta2) * delta.dec[i] * delta.dec[i];
                    dec[i] -= lr * mdec[i] / (Math.sqrt(vdec[i]) + 1e-8);
                }
                for (int i = 0; i < hor.length; i++) {
                    mhor[i] = beta1 * mhor[i] + (1 - beta1) * delta.hor[i];
                    vhor[i] = beta2 * vhor[i] + (1 - beta2) * delta.hor[i] * delta.hor[i];
                    hor[i] -= lr * mhor[i] / (Math.sqrt(vhor[i]) + 1e-8);
                }
                for (int i = 0; i < ver.length; i++) {
                    mver[i] = beta1 * mver[i] + (1 - beta1) * delta.ver[i];
                    vver[i] = beta2 * vver[i] + (1 - beta2) * delta.ver[i] * delta.ver[i];
                    ver[i] -= lr * mver[i] / (Math.sqrt(vver[i]) + 1e-8);
                }
            }

            System.out.printf(Locale.ENGLISH, "%d %.4f %.4f%n", iter, Math.sqrt(mseS), Math.sqrt(mseC));

        }

    }
}
