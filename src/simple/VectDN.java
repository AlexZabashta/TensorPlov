package simple;
import org.apache.commons.lang3.tuple.Pair;

import core.DifferentiableStructure;

public abstract class VectDN<M> implements DifferentiableStructure<Pair<double[], double[]>, M, double[]> {

    final int inputSize, outputSize;

    public VectDN(int inputSize, int outSize) {
        this.inputSize = inputSize;
        this.outputSize = outSize;
    }

    public abstract int weightSize();

    static final double[] emptyArray = new double[0];

    @Override
    public Pair<M, double[]> forward(Pair<double[], double[]> input) {
        double[] x = input.getLeft();
        double[] w = input.getRight();
        assert x.length == inputSize;
        double[] y = new double[outputSize];
        return Pair.of(forward(x, w, y), y);
    }

    public abstract M forward(double[] x, double[] w, double[] y);

    @Override
    public Pair<double[], double[]> backward(M memory, double[] dy) {
        double[] dx = new double[inputSize];
        double[] dw = new double[weightSize()];
        assert dy.length == outputSize;
        backward(memory, dy, dx, dw);
        return null;
    }

    public abstract void backward(M mem, double[] dy, double[] dx, double[] dw);

}
