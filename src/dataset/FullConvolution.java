package dataset;

import java.lang.reflect.Array;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffStruct;
import core.VarDiffStruct;

public class FullConvolution<E, H, V, D> implements DiffStruct<FullConvolution<E, H, V, D>.Input, FullConvolution<E, H, V, D>.Memory, double[]> {

    public final int inputDepth;
    public final VarDiffStruct<double[], E, double[]> encoder;
    public final Convolution<H, V> convolution;
    public final VarDiffStruct<double[], D, double[]> decoder;
    public final int outputDepth;

    public FullConvolution(int inputDepth, VarDiffStruct<double[], E, double[]> encoder, Convolution<H, V> convolution, VarDiffStruct<double[], D, double[]> decoder, int outputDepth) {
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

    public class Memory {
        E[][] encm;
        Convolution<H, V>.Memory convm;
        D decm;

        public Memory(E[][] encm, Convolution<H, V>.Memory convm, D decm) {
            this.encm = encm;
            this.convm = convm;
            this.decm = decm;
        }

    }

    @Override
    public Pair<Memory, double[]> forward(Input input) {
        return forward(input.obj, input.enc, input.hor, input.ver, input.dec);
    }

    public Pair<Memory, double[]> forward(double[][][] obj, double[] enc, double[] hor, double[] ver, double[] dec) {
        final int rows = obj.length, cols = obj[0].length;

        double[][][] enco = new double[rows][cols][];
        @SuppressWarnings("unchecked")
        E[][] encm = (E[][]) Array.newInstance(encoder.memoryClass(), rows, cols);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Pair<E, double[]> encp = encoder.forward(obj[row][col], enc);
                encm[row][col] = encp.getLeft();
                enco[row][col] = encp.getRight();
            }
        }

        Pair<Convolution<H, V>.Memory, double[]> convp = convolution.forward(enco, hor, ver);
        Pair<D, double[]> decp = decoder.forward(convp.getRight(), dec);
        return Pair.of(new Memory(encm, convp.getLeft(), decp.getLeft()), decp.getRight());
    }

    @Override
    public Input backward(Memory memory, double[] deltaOutput) {
        Pair<double[], double[]> decp = decoder.backward(memory.decm, deltaOutput);
        Triple<double[][][], double[], double[]> convp = convolution.backward(memory.convm, decp.getLeft());

        int rows = memory.convm.rows;
        int cols = memory.convm.cols;

        double[][][] dy = convp.getLeft();
        double[][][] dx = new double[rows][cols][];

        double[] enc = new double[encoder.numBoundVars()];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Pair<double[], double[]> encp = encoder.backward(memory.encm[row][col], dy[row][col]);
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<Input> inputClass() {
        return (Class<Input>) new Input(null, null, null, null, null).getClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Memory> memoryClass() {
        return (Class<Memory>) new Memory(null, null, null).getClass();
    }
}
