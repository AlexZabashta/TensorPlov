package tojava;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffStruct;

public abstract class VectorDiffStruct implements DiffStruct<Pair<double[], double[]>, VectorDiffStruct.Memory, double[]>, VectorTransform {

    public final int xSize, wSize, ySize, fSize, bSize;

    public class Memory {
        final double[] x, w, y, f;

        public Memory(double[] x, double[] w, double[] y, double[] f) {
            this.x = x;
            this.w = w;
            this.y = y;
            this.f = f;
        }
    }

    public VectorDiffStruct(int xSize, int wSize, int ySize, int fSize, int bSize) {
        this.xSize = xSize;
        this.wSize = wSize;
        this.ySize = ySize;
        this.fSize = fSize;
        this.bSize = bSize;
    }

    @Override
    public Pair<Memory, double[]> forward(Pair<double[], double[]> input) {
        double[] x = input.getLeft();
        assert x.length == xSize;

        double[] w = input.getRight();
        assert w.length == wSize;

        double[] y = new double[ySize];

        double[] f = new double[fSize];

        forward(x, w, y, f);

        return Pair.of(new Memory(x, w, y, f), y);
    }

    @Override
    public Pair<double[], double[]> backward(Memory memory, double[] dy) {
        double[] x = memory.x;
        assert x.length == xSize;
        double[] dx = new double[xSize];

        double[] w = memory.w;
        assert w.length == wSize;
        double[] dw = new double[wSize];

        double[] y = memory.y;
        assert y.length == ySize;
        assert dy.length == ySize;

        double[] f = memory.f;
        assert f.length == fSize;
        double[] b = new double[bSize];

        backward(x, w, y, dx, dw, dy, f, b);

        return Pair.of(dx, dw);
    }

}
