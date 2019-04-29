package simple;
import org.apache.commons.lang3.tuple.Pair;

public class MultiplyByMatrix extends VectDN<Pair<double[], double[]>> {

    public MultiplyByMatrix(int inputSize, int outSize) {
        super(inputSize, outSize);
    }

    @Override
    public int weightSize() {
        return inputSize * outputSize;
    }

    @Override
    public Pair<double[], double[]> forward(double[] x, double[] w, double[] y) {
        for (int wid = 0, i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++, wid++) {
                y[j] += x[i] * w[wid];
            }
        }
        return Pair.of(x, w);
    }

    @Override
    public void backward(Pair<double[], double[]> xw, double[] dy, double[] dx, double[] dw) {
        double[] x = xw.getLeft();
        double[] w = xw.getRight();

        for (int wid = 0, i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++, wid++) {
                dx[i] += dy[j] * w[wid];
            }
        }
        for (int wid = 0, i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++, wid++) {
                w[wid] += x[i] * dy[j];
            }
        }
    }
}
