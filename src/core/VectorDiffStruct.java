package core;

import org.apache.commons.lang3.tuple.Pair;

public abstract class VectorDiffStruct implements VarDiffStruct<double[], VectorDiffStruct.Memory, double[]>, VectorTransform {

    public class Memory {
        final double[] x, w, y, f;

        public Memory(double[] x, double[] w, double[] y, double[] f) {
            this.x = x;
            this.w = w;
            this.y = y;
            this.f = f;
        }
    }

    public final int xSize, wSize, ySize, fSize, bSize;

    public VectorDiffStruct(int xSize, int wSize, int ySize, int fSize, int bSize) {
        this.xSize = xSize;
        this.wSize = wSize;
        this.ySize = ySize;
        this.fSize = fSize;
        this.bSize = bSize;
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

    @Override
    public Pair<Memory, double[]> forward(double[] x, double[] w) {
        assert x.length == xSize;
        assert w.length == wSize;

        double[] y = new double[ySize];

        double[] f = new double[fSize];

        forward(x, w, y, f);

        return Pair.of(new Memory(x, w, y, f), y);
    }

    @Override
    public Class<Memory> memoryClass() {
        return Memory.class;
    }

    @Override
    public int numBoundVars() {
        return wSize;
    }

}
