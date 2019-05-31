package test.best;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import core.MultiVarDiffStruct;
import core.ParallelVDiffStruct;
import core.Pipe;
import core.Result;
import dataset.Convolution;
import dataset.SymConvolution;
import grad.MAdaGrad;
import test.DataReader;
import test.Dataset;

public class TestNN {

    // final HVFold hvFold = new HVFold();
    final LSTM hvFold = new LSTM();
    final Convolution convolution = new SymConvolution(128, 16, hvFold, hvFold);

    final Encoder encoder = new Encoder();
    final Decoder decoder = new Decoder();
    final Simple simple = new Simple();

    final MultiVarDiffStruct<double[][][], double[][][]> pencoder = MultiVarDiffStruct.convert(new ParallelVDiffStruct(encoder, 128, 16));
    final MultiVarDiffStruct<double[], double[]> mdecoder = MultiVarDiffStruct.convert(decoder);
    final Pipe<double[][][], ?, double[]> net = Pipe.of(pencoder, convolution, mdecoder);

    final double[] enc, dec, hor, ver, sim;

    public TestNN() {

        enc = new double[encoder.numBoundVars()];
        dec = new double[decoder.numBoundVars()];
        hor = new double[hvFold.numBoundVars()];
        ver = new double[hvFold.numBoundVars()];
        sim = new double[simple.numBoundVars()];

        encoder.init(enc);
        decoder.init(dec);
        hvFold.init(hor);
        hvFold.init(ver);
        simple.init(sim);
    }

    class PredictResult {
        final double[] ty;
        final Result<Pair<double[], double[]>, double[]> sp;
        final Result<Pair<double[][][], double[][]>, double[]> cp;

        public PredictResult(double[] ty, Result<Pair<double[], double[]>, double[]> sp, Result<Pair<double[][][], double[][]>, double[]> cp) {
            this.ty = ty;
            this.sp = sp;
            this.cp = cp;
        }

    }

    class Predict implements Callable<PredictResult> {

        final Dataset dataset;

        public Predict(Dataset dataset) {
            this.dataset = dataset;
        }

        @Override
        public PredictResult call() throws Exception {
            Result<Pair<double[], double[]>, double[]> sp = simple.result(dataset.mf, sim);
            Result<Pair<double[][][], double[][]>, double[]> cp = net.result(dataset.dataset, enc, hor, ver, dec);
            return new PredictResult(dataset.lm, sp, cp);
        }
    }

    void run() throws InterruptedException, ExecutionException, IOException {

        ExecutorService executor = Executors.newFixedThreadPool(6);
        List<Dataset> datasets = DataReader.readZipData("csv.zip", executor);

        Pair<List<Dataset>, List<Dataset>> tt = DataReader.splitData("test.txt", datasets);

        List<Dataset> train = tt.getLeft();
        List<Dataset> test = tt.getRight();

        Consumer<double[][]> grad = new MAdaGrad(new double[][] { enc, hor, ver, dec, sim }, 0.0001, 0.9, 0.999);
        // Consumer<double[][]> grad = new MSGD(new double[][] { enc, hor, ver, dec, sim }, 0.001);

        int batch = 12;

        try (PrintWriter out = new PrintWriter("result.txt")) {
            for (int epoch = 1; epoch < 1000; epoch++) {
                double trainS = 0, trainC = 0;
                Collections.shuffle(train);

                int trainPointer = 0;

                while (trainPointer < train.size()) {
                    List<Future<PredictResult>> results = new ArrayList<>();
                    for (int i = 0; trainPointer < train.size() && i < batch; i++) {
                        results.add(executor.submit(new Predict(train.get(trainPointer++))));
                    }

                    List<Future<double[][]>> fdeltas = new ArrayList<>();

                    for (Future<PredictResult> future : results) {
                        PredictResult result = future.get();

                        double[] sy = result.sp.value();
                        double[] cy = result.cp.value();

                        double[] dys = new double[3];
                        double[] dyc = new double[3];

                        for (int i = 0; i < 3; i++) {
                            dys[i] = sy[i] - result.ty[i];
                            dyc[i] = cy[i] - result.ty[i];
                        }

                        trainS += (dys[0] * dys[0] + dys[1] * dys[1] + dys[2] * dys[2]) / 3;
                        trainC += (dyc[0] * dyc[0] + dyc[1] * dyc[1] + dyc[2] * dyc[2]) / 3;

                        fdeltas.add(executor.submit(new Callable<double[][]>() {

                            @Override
                            public double[][] call() throws Exception {
                                double[][] delta = Arrays.copyOf(result.cp.apply(dyc).getRight(), 5);
                                delta[4] = result.sp.apply(dys).getRight();
                                return delta;
                            }
                        }));
                    }
                    System.out.printf(Locale.ENGLISH, "%d train %.4f %.4f%n", epoch, Math.sqrt(trainS / trainPointer), Math.sqrt(trainC / trainPointer));

                    List<double[][]> deltas = new ArrayList<>();
                    for (Future<double[][]> fdelta : fdeltas) {
                        deltas.add(fdelta.get());
                    }

                    for (double[][] delta : deltas) {
                        grad.accept(delta);
                    }
                }

                String trainResult = String.format(Locale.ENGLISH, "%d train %.4f %.4f", epoch, Math.sqrt(trainS / train.size()), Math.sqrt(trainC / train.size()));
                System.out.println(trainResult);
                out.println(trainResult);
                out.flush();

                List<Future<double[]>> results = new ArrayList<>();

                for (Dataset dataset : test) {
                    results.add(executor.submit(new Callable<double[]>() {
                        @Override
                        public double[] call() throws Exception {
                            PredictResult result = (new Predict(dataset)).call();
                            double[] sy = result.sp.value();
                            double[] cy = result.cp.value();

                            double[] dys = new double[3];
                            double[] dyc = new double[3];

                            for (int i = 0; i < 3; i++) {
                                dys[i] = sy[i] - result.ty[i];
                                dyc[i] = cy[i] - result.ty[i];
                            }

                            return new double[] { (dys[0] * dys[0] + dys[1] * dys[1] + dys[2] * dys[2]) / 3, (dyc[0] * dyc[0] + dyc[1] * dyc[1] + dyc[2] * dyc[2]) / 3 };
                        }
                    }));
                }

                double testS = 0, testC = 0;

                for (Future<double[]> future : results) {
                    double[] result = future.get();
                    testS += result[0];
                    testC += result[1];
                }

                String testResult = String.format(Locale.ENGLISH, "%d test %.4f %.4f", epoch, Math.sqrt(testS / test.size()), Math.sqrt(testC / test.size()));
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
