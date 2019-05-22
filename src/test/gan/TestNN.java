package test.gan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import core.MultiVarDiffStruct;
import core.Result;
import grad.MAdaGrad;
import ru.ifmo.ctddev.ml.mfe.JointDecMF;
import test.DataReader;
import test.Dataset;

public class TestNN {

    MultiVarDiffStruct<Pair<double[][][], double[]>, double[]> dis = BuildGanUnits.discriminator();
    MultiVarDiffStruct<double[], double[][][]> gen = BuildGanUnits.generator();

    final double[][] disValues = dis.genBoundVars();
    final double[][] genValues = gen.genBoundVars();

    final int features = DataReader.numFeatures;
    final int objects = DataReader.numObjects;

    class Outcome {
        final double realRate, mfMSE, lmMSE;
        final double[][] deltaGen, deltaDisR, deltaDisF;

        public Outcome(double realRate, double mfMSE, double lmMSE) {
            this(realRate, mfMSE, lmMSE, null, null, null);
        }

        public Outcome(double realRate, double mfMSE, double lmMSE, double[][] deltaGen, double[][] deltaDisR, double[][] deltaDisF) {
            this.realRate = realRate;
            this.mfMSE = mfMSE;
            this.lmMSE = lmMSE;
            this.deltaGen = deltaGen;
            this.deltaDisR = deltaDisR;
            this.deltaDisF = deltaDisF;
        }
    }

    void run() throws InterruptedException, FileNotFoundException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(6);

        Pair<List<Dataset>, List<Dataset>> datasets = DataReader.readData("csv", executor);
        List<Dataset> train = datasets.getLeft();
        List<Dataset> test = datasets.getRight();

        Consumer<double[][]> disGrad = new MAdaGrad(disValues, 0.001, 0.9, 0.999);
        Consumer<double[][]> genGrad = new MAdaGrad(genValues, 0.001, 0.9, 0.999);

        int batch = 6;

        try (PrintWriter out = new PrintWriter("result.txt")) {
            for (int epoch = 1; epoch < 1000; epoch++) {
                double trainG = 0, trainR = 0, trainL = 0;

                Collections.shuffle(train);

                int trainPointer = 0;

                while (trainPointer < train.size()) {
                    List<Future<Outcome>> results = new ArrayList<>();
                    for (int i = 0; trainPointer < train.size() && i < batch; i++) {
                        Dataset dataset = train.get(trainPointer++);

                        results.add(executor.submit(new Callable<Outcome>() {

                            @Override
                            public Outcome call() throws Exception {
                                Random random = new Random();
                                double[] vector = Arrays.copyOf(dataset.mf, 46);
                                for (int i = 23; i < 46; i++) {
                                    vector[i] = random.nextGaussian();
                                }
                                Result<Pair<double[], double[][]>, double[][][]> gens = gen.result(vector, genValues);
                                double realRate = 0, mfMSE = 0, lmMSE = 0;

                                double[][][] obj = gens.value();

                                double[][] data = new double[objects][features];
                                int[] labels = new int[objects];

                                for (int oid = 0; oid < objects; oid++) {
                                    for (int fid = 0; fid < features; fid++) {
                                        data[oid][fid] = obj[oid][fid][0];
                                    }
                                    labels[oid] = (2 * oid < objects) ? 0 : 1;
                                }

                                double[] joint = JointDecMF.extract(objects, features, 2, data, labels);

                                for (int i = 0; i < 23; i++) {
                                    double diff = joint[i] - vector[i];
                                    mfMSE += diff * diff;
                                }

                                double[] fmf = Arrays.copyOf(joint, 23);

                                Result<Pair<Pair<double[][][], double[]>, double[][]>, double[]> disf = dis.result(Pair.of(obj, fmf), disValues);

                                double[] disfv = disf.value();
                                if (disfv[3] > 0) {
                                    realRate += 0.5;
                                }

                                double[] disfd = new double[4];
                                disfd[0] = disfv[0] - joint[23];
                                disfd[1] = disfv[1] - joint[24];
                                disfd[2] = disfv[2] - joint[25];
                                disfd[3] = disfv[3] - 1;

                                double[][][] deltaObj = disf.apply(disfd).getLeft().getLeft();

                                disfd[0] /= Math.sqrt(2);
                                disfd[1] /= Math.sqrt(2);
                                disfd[2] /= Math.sqrt(2);
                                disfd[3] = disfv[3] + 1;
                                Pair<Pair<double[][][], double[]>, double[][]> disfdd = disf.apply(disfd);

                                Pair<double[], double[][]> gend = gens.apply(deltaObj);

                                Result<Pair<Pair<double[][][], double[]>, double[][]>, double[]> disr = dis.result(Pair.of(dataset.dataset, dataset.mf), disValues);

                                double[] disrv = disr.value();
                                double[] disrd = new double[4];

                                disrd[0] = disrv[0] - dataset.lm[0];
                                disrd[1] = disrv[1] - dataset.lm[1];
                                disrd[2] = disrv[2] - dataset.lm[2];
                                disrd[3] = disrv[3] - 1;

                                if (disrv[3] <= 0) {
                                    realRate += 0.5;
                                }
                                lmMSE += (disrd[0] * disrd[0] + disrd[1] * disrd[1] + disrd[2] * disrd[2]) / 3;

                                Pair<Pair<double[][][], double[]>, double[][]> disrrd = disr.apply(disrd);

                                return new Outcome(realRate, mfMSE, lmMSE, gend.getRight(), disrrd.getRight(), disfdd.getRight());
                            }
                        }));
                    }

                    List<double[][]> deltaG = new ArrayList<>();
                    List<double[][]> deltaD = new ArrayList<>();

                    for (Future<Outcome> future : results) {
                        Outcome outcome = future.get();
                        trainG += outcome.mfMSE;
                        trainL += outcome.lmMSE;
                        trainR += outcome.realRate;
                        deltaD.add(outcome.deltaDisF);
                        deltaD.add(outcome.deltaDisR);
                        deltaG.add(outcome.deltaGen);

                    }

                    for (double[][] delta : deltaG) {
                        genGrad.accept(delta);
                    }

                    for (double[][] delta : deltaD) {
                        disGrad.accept(delta);
                    }

                    System.out.printf(Locale.ENGLISH, "%d train %.4f %.4f %.4f%n", epoch, trainG / trainPointer, trainL / trainPointer, trainR / trainPointer);

                }

                String trainResult = String.format(Locale.ENGLISH, "%d train %.4f %.4f %.4f%n", epoch, trainG / train.size(), trainL / train.size(), trainR / train.size());
                System.out.println(trainResult);
                out.println(trainResult);
                out.flush();

                List<Future<Outcome>> results = new ArrayList<>();

                for (Dataset dataset : test) {
                    results.add(executor.submit(new Callable<Outcome>() {

                        @Override
                        public Outcome call() throws Exception {
                            Random random = new Random();
                            double[] vector = Arrays.copyOf(dataset.mf, 46);
                            for (int i = 23; i < 46; i++) {
                                vector[i] = random.nextGaussian();
                            }
                            Result<Pair<double[], double[][]>, double[][][]> gens = gen.result(vector, genValues);
                            double realRate = 0, mfMSE = 0, lmMSE = 0;

                            double[][][] obj = gens.value();

                            double[][] data = new double[objects][features];
                            int[] labels = new int[objects];

                            for (int oid = 0; oid < objects; oid++) {
                                for (int fid = 0; fid < features; fid++) {
                                    data[oid][fid] = obj[oid][fid][0];
                                }
                                labels[oid] = (2 * oid < objects) ? 0 : 1;
                            }

                            double[] joint = JointDecMF.extract(objects, features, 2, data, labels);

                            for (int i = 0; i < 23; i++) {
                                double diff = joint[i] - vector[i];
                                mfMSE += diff * diff;
                            }

                            double[] fmf = Arrays.copyOf(joint, 23);

                            Result<Pair<Pair<double[][][], double[]>, double[][]>, double[]> disf = dis.result(Pair.of(obj, fmf), disValues);

                            double[] disfv = disf.value();

                            if (disfv[3] > 0) {
                                realRate += 0.5;
                            }

                            Result<Pair<Pair<double[][][], double[]>, double[][]>, double[]> disr = dis.result(Pair.of(dataset.dataset, dataset.mf), disValues);

                            double[] disrv = disr.value();
                            double[] disrd = new double[4];

                            disrd[0] = disrv[0] - dataset.lm[0];
                            disrd[1] = disrv[1] - dataset.lm[1];
                            disrd[2] = disrv[2] - dataset.lm[2];

                            if (disrv[3] <= 0) {
                                realRate += 0.5;
                            }
                            lmMSE += (disrd[0] * disrd[0] + disrd[1] * disrd[1] + disrd[2] * disrd[2]) / 3;

                            return new Outcome(realRate, mfMSE, lmMSE);
                        }
                    }));
                }

                double testG = 0, testR = 0, testL = 0;
                for (Future<Outcome> future : results) {
                    Outcome outcome = future.get();
                    testG += outcome.mfMSE;
                    testL += outcome.lmMSE;
                    testR += outcome.realRate;
                }

                String testResult = String.format(Locale.ENGLISH, "%d test %.4f %.4f %.4f%n", epoch, testG / test.size(), testL / test.size(), testR / test.size());
                System.out.println(testResult);
                out.println(testResult);
                out.flush();

            }
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        new TestNN().run();
    }
}
