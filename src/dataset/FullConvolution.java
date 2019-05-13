package dataset;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffFunct;
import core.Result;
import core.VarDiffStruct;

public class FullConvolution implements DiffFunct<FullConvolution.Input, double[]> {

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

    public class Input {
        public double[][][] obj;
        public double[] enc, hor, ver, dec;

        public Input(double[][][] obj, double[] enc, double[] hor, double[] ver, double[] dec) {
            this.obj = obj;
            this.enc = enc;
            this.hor = hor;
            this.ver = ver;
            this.dec = dec;
        }

    }

    public Input input(double[][][] obj, double[] enc, double[] hor, double[] ver, double[] dec) {
        return new Input(obj, enc, hor, ver, dec);
    }

    @Override
    public Result<Input, double[]> result(Input input) {
        return result(input.obj, input.enc, input.hor, input.ver, input.dec);
    }

    public Result<Input, double[]> result(double[][][] obj, double[] enc, double[] hor, double[] ver, double[] dec) {
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

        Result<Triple<double[][][], double[], double[]>, double[]> convp = convolution.result(enco, hor, ver);
        Function<double[], Triple<double[][][], double[], double[]>> convm = convp.derivative();

        Result<Pair<double[], double[]>, double[]> decp = decoder.result(convp.value(), dec);
        Function<double[], Pair<double[], double[]>> decm = decp.derivative();

        return new Result<FullConvolution.Input, double[]>(new Function<double[], Input>() {

            @Override
            public Input apply(double[] deltaOutput) {

                Pair<double[], double[]> decp = decm.apply(deltaOutput);

                Triple<double[][][], double[], double[]> convp = convm.apply(decp.getLeft());

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

                return new Input(dx, enc, convp.getMiddle(), convp.getRight(), decp.getRight());

            }

        }, decp.value());

    }

}
