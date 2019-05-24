package core;

public interface VectorTransform {
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b);

    public void forward(double[] x, double[] w, double[] y, double[] f);

    public default void init(double[] w) {

    }

}
