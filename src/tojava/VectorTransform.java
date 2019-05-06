package tojava;

public interface VectorTransform {
    public void forward(double[] x, double[] w, double[] y, double[] f);

    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b);

    public default void init(double[] w) {

    }

}
