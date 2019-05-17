package grad;

import java.util.function.Consumer;

public class MSGD implements Consumer<double[][]> {
    final double[][] values;
    final double learningRate;

    double betaMt = 1, betaVt = 1;

    public MSGD(double[][] values, double learningRate) {
        this.values = values;
        this.learningRate = learningRate;
    }

    @Override
    public void accept(double[][] delta) {
        synchronized (values) {
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[i].length; j++) {
                    values[i][j] -= learningRate * delta[i][j];
                }
            }
        }
    }
}
