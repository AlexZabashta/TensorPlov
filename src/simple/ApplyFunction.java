package simple;

import java.util.function.DoubleUnaryOperator;

import org.apache.commons.lang3.tuple.Pair;

public class ApplyFunction extends VectDN<double[]> {
    final DoubleUnaryOperator f, df;

    public ApplyFunction(int size, DoubleUnaryOperator f, DoubleUnaryOperator df) {
        super(size, size);
        this.f = f;
        this.df = df;

    }

    @Override
    public int weightSize() {
        return 0;
    }

    @Override
    public double[] forward(double[] x, double[] w, double[] y) {
        for (int i = 0; i < outputSize; i++) {
            y[i] = f.applyAsDouble(x[i]);
        }
        return x;
    }

    @Override
    public void backward(double[] x, double[] dy, double[] dx, double[] dw) {
        for (int i = 0; i < inputSize; i++) {
            dx[i] = dy[i] * df.applyAsDouble(x[i]);
        }
    }

    @Override
    public Class<Pair<double[], double[]>> inputClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<double[]> memoryClass() {
        // TODO Auto-generated method stub
        return null;
    }

}
