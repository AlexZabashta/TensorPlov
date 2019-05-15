import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToDoubleFunction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;

import clsf.Dataset;
import core.MultiVarDiffStruct;
import core.ParallelVDiffStruct;
import core.Pipe;
import core.Result;
import dataset.Convolution;
import dataset.SymConvolution;
import grad.MAdaGrad;
import mfextraction.CMFExtractor;
import mfextraction.KNNLandMark;

public class TestNN {
    public static void main(String[] args) throws IOException, InterruptedException {

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

        CMFExtractor extractor = new CMFExtractor();
        ToDoubleFunction<Dataset> knnScore = new KNNLandMark();
        int numMF = extractor.length();

        final int numData = datasets.size();
        List<Dataset> train = new ArrayList<>();
        List<Dataset> test = new ArrayList<>();

        final double[] min = new double[numMF];
        final double[] max = new double[numMF];

        Arrays.fill(min, Double.POSITIVE_INFINITY);
        Arrays.fill(max, Double.NEGATIVE_INFINITY);

        for (int i = 0; i < numData; i++) {
            double[] vector = extractor.apply(datasets.get(i));
            for (int j = 0; j < numMF; j++) {
                min[j] = Math.min(min[j], vector[j]);
                max[j] = Math.max(max[j], vector[j]);
            }
        }

        Collections.sort(datasets, Comparator.comparing(dataset -> dataset.name));

        for (int i = 0; i < numData; i++) {
            if (i % 10 == 0) {
                test.add(datasets.get(i));
            } else {
                train.add(datasets.get(i));
            }
        }

        System.out.println(numData);
        HVFold hvFold = new HVFold();
        Convolution convolution = new SymConvolution(40, hvFold, hvFold);

        Encoder encoder = new Encoder();
        Decoder decoder = new Decoder();
        Simple simple = new Simple();

        MultiVarDiffStruct<double[][][], double[][][]> pencoder = MultiVarDiffStruct.convert(new ParallelVDiffStruct(true, encoder));
        MultiVarDiffStruct<double[], double[]> mdecoder = MultiVarDiffStruct.convert(decoder);
        Pipe<double[][][], ?, double[]> net = Pipe.of(pencoder, convolution, mdecoder);

        double[] enc = new double[encoder.numBoundVars()];
        double[] dec = new double[decoder.numBoundVars()];
        double[] hor = new double[hvFold.numBoundVars()];
        double[] ver = new double[hvFold.numBoundVars()];
        double[] sim = new double[simple.numBoundVars()];

        MAdaGrad grad = new MAdaGrad(new double[][] { enc, hor, ver, dec, sim }, 0.002, 0.9, 0.999);

        encoder.init(enc);
        decoder.init(dec);
        hvFold.init(hor);
        hvFold.init(ver);
        simple.init(sim);

        int batch = 6;

        try (PrintWriter out = new PrintWriter("result.txt")) {
            for (int epoch = 1; epoch < 100; epoch++) {
                double trainS = 0, trainC = 0, cntTrain = 0;

                Collections.shuffle(train);
                AtomicInteger trainPointer = new AtomicInteger(0);

                while (trainPointer.get() < train.size()) {
                    double[][][] delta = new double[batch][][];
                    double[] diffS = new double[batch];
                    double[] diffC = new double[batch];

                    Arrays.fill(diffS, Double.NaN);
                    Arrays.fill(diffC, Double.NaN);

                    Thread[] thread = new Thread[batch];
                    for (int tid = 0; tid < batch; tid++) {
                        final int did = tid;

                        thread[tid] = new Thread() {
                            @Override
                            public void run() {
                                Dataset dataset = null;
                                synchronized (train) {
                                    if (trainPointer.get() < train.size()) {
                                        dataset = train.get(trainPointer.getAndIncrement());
                                    }
                                }
                                if (dataset == null) {
                                    return;
                                }

                                double[] rmf = extractor.apply(dataset);
                                double[] nmf = new double[numMF];

                                for (int i = 0; i < numMF; i++) {
                                    nmf[i] = (rmf[i] - min[i]) / (max[i] - min[i]) * 2 - 1;
                                }

                                Result<Pair<double[], double[]>, double[]> sp = simple.result(nmf, sim);

                                double[] ys = sp.value();
                                double ty = knnScore.applyAsDouble(dataset) * 2 - 1;
                                double diffs = ys[0] - ty;

                                synchronized (diffS) {
                                    diffS[did] = diffs;
                                }

                                double[] dys = { diffs };

                                double[] deltas = sp.apply(dys).getRight();

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

                                synchronized (diffC) {
                                    diffC[did] = diffc;
                                }

                                double[] dyc = { diffc };

                                double[][] delta = Arrays.copyOf(cp.derivative().apply(dyc).getRight(), 5);
                                delta[4] = deltas;

                            };
                        };
                        thread[tid].start();
                    }

                    for (int tid = 0; tid < batch; tid++) {
                        thread[tid].join();
                    }

                    for (int tid = 0; tid < batch; tid++) {
                        if (delta[tid] != null && Double.isFinite(diffS[tid]) && Double.isFinite(diffC[tid])) {
                            trainS += diffS[tid] * diffS[tid];
                            trainC += diffC[tid] * diffC[tid];
                            cntTrain += 1;
                            grad.accept(delta[tid]);
                        }
                    }
                }
                String trainResult = String.format(Locale.ENGLISH, "%d train %.4f %.4f", epoch, Math.sqrt(trainS / cntTrain), Math.sqrt(trainC / cntTrain));
                System.out.println(trainResult);
                out.println(trainResult);
                out.flush();

                double testS = 0, testC = 0, cntTest = 0;

                AtomicInteger testPointer = new AtomicInteger(0);

                while (testPointer.get() < test.size()) {
                    double[] diffS = new double[batch];
                    double[] diffC = new double[batch];

                    Arrays.fill(diffS, Double.NaN);
                    Arrays.fill(diffC, Double.NaN);

                    Thread[] thread = new Thread[batch];
                    for (int tid = 0; tid < batch; tid++) {
                        final int did = tid;

                        thread[tid] = new Thread() {
                            @Override
                            public void run() {
                                Dataset dataset = null;
                                synchronized (test) {
                                    if (testPointer.get() < test.size()) {
                                        dataset = test.get(testPointer.getAndIncrement());
                                    }
                                }
                                if (dataset == null) {
                                    return;
                                }

                                double[] rmf = extractor.apply(dataset);
                                double[] nmf = new double[numMF];

                                for (int i = 0; i < numMF; i++) {
                                    nmf[i] = (rmf[i] - min[i]) / (max[i] - min[i]) * 2 - 1;
                                }

                                Result<Pair<double[], double[]>, double[]> sp = simple.result(nmf, sim);

                                double[] ys = sp.value();
                                double ty = knnScore.applyAsDouble(dataset) * 2 - 1;
                                double diffs = ys[0] - ty;

                                synchronized (diffS) {
                                    diffS[did] = diffs;
                                }

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

                                synchronized (diffC) {
                                    diffC[did] = diffc;
                                }
                            };
                        };
                        thread[tid].start();
                    }

                    for (int tid = 0; tid < batch; tid++) {
                        thread[tid].join();
                    }

                    for (int tid = 0; tid < batch; tid++) {
                        if (Double.isFinite(diffS[tid]) && Double.isFinite(diffC[tid])) {
                            testS += diffS[tid] * diffS[tid];
                            testC += diffC[tid] * diffC[tid];
                            cntTest += 1;
                        }
                    }
                }

                String testResult = String.format(Locale.ENGLISH, "%d test %.4f %.4f", epoch, Math.sqrt(testS / cntTest), Math.sqrt(testC / cntTest));
                System.out.println(testResult);
                out.println(testResult);
                out.flush();

            }
        }
    }
}
