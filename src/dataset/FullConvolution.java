package dataset;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffStruct;
import core.VarDiffStruct;

public class FullConvolution<E, H, V, D> implements DiffStruct<FullConvolution<E, H, V, D>.Input, FullConvolution<E, H, V, D>.Memory, double[]> {

    public final int inputDepth;
    public final VarDiffStruct<Pair<double[], double[]>, E, double[]> encoder;
    public final Convolution<H, V> convolution;
    public final VarDiffStruct<Pair<double[], double[]>, D, double[]> decoder;
    public final int outputDepth;

    public FullConvolution(int inputDepth, VarDiffStruct<Pair<double[], double[]>, E, double[]> encoder, Convolution<H, V> convolution, VarDiffStruct<Pair<double[], double[]>, D, double[]> decoder, int outputDepth) {
        this.inputDepth = inputDepth;
        this.encoder = encoder;
        this.convolution = convolution;
        this.decoder = decoder;
        this.outputDepth = outputDepth;
    }

    public class Input {
        double[][][] input;
        double[] enc, hor, ver, dec;

        public Input(double[][][] input, double[] enc, double[] hor, double[] ver, double[] dec) {
            this.input = input;
            this.enc = enc;
            this.hor = hor;
            this.ver = ver;
            this.dec = dec;
        }

    }

    public Input input(double[][][] input, double[] enc, double[] hor, double[] ver, double[] dec) {
        return new Input(input, enc, hor, ver, dec);
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
    public Pair<FullConvolution<E, H, V, D>.Memory, double[]> forward(FullConvolution<E, H, V, D>.Input input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FullConvolution<E, H, V, D>.Input backward(FullConvolution<E, H, V, D>.Memory memory, double[] deltaOutput) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<FullConvolution<E, H, V, D>.Input> inputClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<FullConvolution<E, H, V, D>.Memory> memoryClass() {
        // TODO Auto-generated method stub
        return null;
    }
}
