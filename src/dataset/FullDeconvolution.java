package dataset;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import core.MultiVarDiffStruct;
import core.Result;
import core.VarDiffStruct;

public class FullDeconvolution implements MultiVarDiffStruct<double[], double[][][]> {

    public final int inputDepth;
    public final VarDiffStruct<double[], double[]> encoder;
    public final Deconvolution deconvolution;
    public final VarDiffStruct<double[], double[]> decoder;
    public final int outputDepth;

    public FullDeconvolution(int inputDepth, VarDiffStruct<double[], double[]> encoder, Deconvolution deconvolution, VarDiffStruct<double[], double[]> decoder, int outputDepth) {
        this.inputDepth = inputDepth;
        this.encoder = encoder;
        this.deconvolution = deconvolution;
        this.decoder = decoder;
        this.outputDepth = outputDepth;
    }

    @Override
    public Result<Pair<double[], double[][]>, double[][][]> result(double[] freeVar, double[]... bounVar) {
        return result(freeVar, bounVar[0], bounVar[1], bounVar[2], bounVar[3]);
    }

    public Result<Pair<double[], double[][]>, double[][][]> result(double[] vector, double[] enc, double[] hor, double[] ver, double[] dec) {

        Result<Pair<double[], double[]>, double[]> encr = encoder.result(vector, enc);
        Function<double[], Pair<double[], double[]>> encd = encr.derivative();

        Result<Pair<double[], double[][]>, double[][][]> dcvr = deconvolution.result(encr.value(), hor, ver);
        Function<double[][][], Pair<double[], double[][]>> dcvd = dcvr.derivative();

        double[][][] obj = dcvr.value();
        double[][][] dataset = new double[deconvolution.rows][deconvolution.cols][];

        @SuppressWarnings("unchecked")
        Function<double[], Pair<double[], double[]>>[][] decd = new Function[deconvolution.rows][deconvolution.cols];

        for (int row = 0; row < deconvolution.rows; row++) {
            for (int col = 0; col < deconvolution.cols; col++) {
                Result<Pair<double[], double[]>, double[]> decr = decoder.result(obj[row][col], dec);
                decd[row][col] = decr.derivative();
                dataset[row][col] = decr.value();
            }
        }

        return new Result<Pair<double[], double[][]>, double[][][]>(new Function<double[][][], Pair<double[], double[][]>>() {

            @Override
            public Pair<double[], double[][]> apply(double[][][] deltaDataset) {
                double[][][] deltaObj = new double[deconvolution.rows][deconvolution.cols][];

                double[] sdd = new double[decoder.numBoundVars()];

                for (int row = 0; row < deconvolution.rows; row++) {
                    for (int col = 0; col < deconvolution.cols; col++) {
                        double sum = 0;
                        for (int i = 0; i < outputDepth; i++) {
                            sum += deltaDataset[row][col][i] * deltaDataset[row][col][i];
                        }
                        double error = Math.sqrt(sum);

                        Pair<double[], double[]> dxd = decd[row][col].apply(deltaDataset[row][col]);
                        deltaObj[row][col] = dxd.getLeft();

                        double[] dd = dxd.getRight();
                        for (int i = 0; i < dd.length; i++) {
                            sdd[i] += dd[i];
                        }

                        for (int i = 0; i < deconvolution.depth; i++) {
                            deltaObj[row][col][i] *= error;
                        }
                    }
                }
                double normSdd = 1.0 / (deconvolution.rows * deconvolution.cols);
                for (int i = 0; i < sdd.length; i++) {
                    sdd[i] *= normSdd;
                }

                // int x = dcvd.apply(deltaObj);

                return Pair.of(null, new double[][] { sdd });
            }

        }, dataset);

    }

    @Override
    public int[] numBoundVars() {
        return new int[] { encoder.numBoundVars(), deconvolution.horzExpand.numBoundVars(), deconvolution.vertExpand.numBoundVars(), decoder.numBoundVars() };
    }

}
