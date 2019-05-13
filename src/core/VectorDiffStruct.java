package core;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public abstract class VectorDiffStruct implements VarDiffStruct<double[], double[]>, VectorTransform {

    public class Memory implements Function<double[], Pair<double[], double[]>> {
        final double[] x, w, y, f;

        public Memory(double[] x, double[] w, double[] y, double[] f) {
            this.x = x;
            assert x.length == xSize;
            this.w = w;
            assert w.length == wSize;
            this.y = y;
            assert y.length == ySize;
            this.f = f;
            assert f.length == fSize;
        }

        @Override
        public Pair<double[], double[]> apply(double[] dy) {
            assert dy.length == ySize;

            double[] dx = new double[xSize];
            double[] dw = new double[wSize];
            double[] b = new double[bSize];

            VectorDiffStruct.this.backward(x, w, y, dx, dw, dy, f, b);

            return Pair.of(dx, dw);
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
    public Result<Pair<double[], double[]>, double[]> result(double[] x, double[] w) {
        assert x.length == xSize;
        assert w.length == wSize;

        double[] y = new double[ySize];
        double[] f = new double[fSize];

        forward(x, w, y, f);

        return new Result<>(new Memory(x, w, y, f), y);
    }

    @Override
    public int numBoundVars() {
        return wSize;
    }

}
