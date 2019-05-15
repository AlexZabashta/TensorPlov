import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
import dataset.SymConvolution;
import grad.AdaGrad;
import grad.MAdaGrad;
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

        AdaGrad sGrad = new AdaGrad(sim, 0.1, 0.9, 0.99);
        MAdaGrad cGrad = new MAdaGrad(new double[][] { enc, hor, ver, dec }, 0.1, 0.9, 0.99);

        encoder.init(enc);
        decoder.init(dec);
        hvFold.init(hor);
        hvFold.init(ver);
        simple.init(sim);

        int batchs = 10;
        double alpha = 0.97;

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

        try (PrintWriter out = new PrintWriter("result.txt")) {

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

                    double[] deltas = sp.apply(dys).getRight();
                    sGrad.accept(deltas);

                    mseS = alpha * mseS + (1 - alpha) * diffs * diffs;

                    double[][][] obj = new double[numObjects][numFeatures][2];

                    for (int oid = 0; oid < numObjects; oid++) {
                        for (int fid = 0; fid < numFeatures; fid++) {
                            obj[oid][fid][0] = dataset.data[oid][fid];
                            obj[oid][fid][1] = dataset.labels[oid];
                        }
                    }

                    Result<Pair<double[][][], double[][]>, double[]> cp = net.result(obj, enc, hor, ver, dec);

                    double[] yc = cp.value();
                    double diffc = yc[0] - ty;

                    mseC = alpha * mseC + (1 - alpha) * diffc * diffc;

                    double[] dyc = { diffc };

                    Pair<double[][][], double[][]> delta = cp.derivative().apply(dyc);
                    cGrad.accept(delta.getRight());
                }

                String string = String.format(Locale.ENGLISH, "%d %.4f %.4f", iter, Math.sqrt(mseS), Math.sqrt(mseC));

                System.out.println(string);
                out.println(string);
                out.flush();

            }
        }

    }
}
