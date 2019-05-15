package dataset;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import core.MultiVarDiffStruct;
import core.Result;
import core.VarDiffStruct;

public class FullConvolution implements MultiVarDiffStruct<double[][][], double[]> {

    public final int inputDepth;
    public final VarDiffStruct<double[], double[]> encoder;
    public final Convolution convolution;
    public final VarDiffStruct<double[], double[]> decoder;
    public final int outputDepth;

    public FullConvolution(int inputDepth, VarDiffStruct<double[], double[]> encoder, Convolution convolution, VarDiffStruct<double[], double[]> decoder, int outputDepth) {
        this.inputDepth = inputDepth;
        this.encoder = encoder;
        this.convolution = convolution;
        this.decoder = decoder;
        this.outputDepth = outputDepth;
    }

    public Result<Pair<double[][][], double[][]>, double[]> result(double[][][] obj, double[] enc, double[] hor, double[] ver, double[] dec) {
        final int rows = obj.length, cols = obj[0].length;

        double[][][] enco = new double[rows][cols][];

        @SuppressWarnings("unchecked")
        Function<double[], Pair<double[], double[]>>[][] encm = new Function[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Result<Pair<double[], double[]>, double[]> encp = encoder.result(obj[row][col], enc);
                encm[row][col] = encp.derivative();
                enco[row][col] = encp.value();
            }
        }

        Result<Pair<double[][][], double[][]>, double[]> convp = convolution.result(enco, hor, ver);
        Function<double[], Pair<double[][][], double[][]>> convm = convp.derivative();

        Result<Pair<double[], double[]>, double[]> decp = decoder.result(convp.value(), dec);
        Function<double[], Pair<double[], double[]>> decm = decp.derivative();

        return new Result<Pair<double[][][], double[][]>, double[]>(new Function<double[], Pair<double[][][], double[][]>>() {

            @Override
            public Pair<double[][][], double[][]> apply(double[] deltaOutput) {

                Pair<double[], double[]> decp = decm.apply(deltaOutput);

                Pair<double[][][], double[][]> convp = convm.apply(decp.getLeft());

                // TODO transfer error

                double[][][] dy = convp.getLeft();
                double[][][] dx = new double[rows][cols][];

                double[] enc = new double[encoder.numBoundVars()];

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        Pair<double[], double[]> encp = encm[row][col].apply(dy[row][col]);
                        dx[row][col] = encp.getLeft();

                        double[] encd = encp.getRight();

                        for (int i = 0; i < enc.length; i++) {
                            enc[i] += encd[i];
                        }
                    }
                }

                for (int i = 0; i < enc.length; i++) {
                    enc[i] /= rows * cols;
                }

                double[][] dhv = convp.getRight();

                return Pair.of(dx, new double[][] { enc, dhv[0], dhv[1], decp.getRight() });
            }

        }, decp.value());

    }

    @Override
    public int[] numBoundVars() {
        return new int[] { encoder.numBoundVars(), convolution.horzFold.numBoundVars(), convolution.vertFold.numBoundVars(), decoder.numBoundVars() };
    }

    @Override
    public Result<Pair<double[][][], double[][]>, double[]> result(double[][][] freeVar, double[]... bounVar) {
        return result(freeVar, bounVar[0], bounVar[1], bounVar[2], bounVar[3]);
    }

}
