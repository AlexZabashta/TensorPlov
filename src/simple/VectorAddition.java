package simple;
public class VectorAddition extends VectDN<double[]> {

    public VectorAddition(int inputSize, int outSize) {
        super(inputSize, outSize);
    }

    @Override
    public int weightSize() {
        return outputSize;
    }

    @Override
    public double[] forward(double[] x, double[] w, double[] y) {
        for (int i = 0; i < outputSize; i++) {
            y[i] = x[i] + w[i];
        }
        return x;
    }

    @Override
    public void backward(double[] x, double[] dy, double[] dx, double[] dw) {
        for (int i = 0; i < inputSize; i++) {
            dx[i] = dy[i];
            dw[i] += x[i] * dy[i];
        }
    }
}
