package core;

import java.util.Objects;

public class SeqVectorTransform extends VectorDiffStruct {

    public final VectorTransform[] transforms;

    public SeqVectorTransform(int xSize, int wSize, int ySize, int fSize, int bSize, VectorTransform... transforms) {
        super(xSize, wSize, ySize, fSize, bSize);
        this.transforms = transforms;
        for (VectorTransform transform : transforms) {
            Objects.requireNonNull(transform);
        }
    }

    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        for (VectorTransform transform : transforms) {
            transform.forward(x, w, y, f);
        }
    }

    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        for (int i = transforms.length - 1; i >= 0; i--) {
            transforms[i].backward(x, w, y, dx, dw, dy, f, b);
        }
    }

    @Override
    public void init(double[] w) {
        for (VectorTransform transform : transforms) {
            transform.init(w);
        }
    }

}
