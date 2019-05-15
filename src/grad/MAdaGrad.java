package grad;

import java.util.Arrays;
import java.util.function.Consumer;

public class MAdaGrad implements Consumer<double[][]> {
    final double[][] values;
    final double[][] mu, sigma;
    final double betaM, betaS;
    final double learningRate;

    public MAdaGrad(double[][] values, double learningRate, double betaM, double betaS) {
        this.values = values;
        this.learningRate = learningRate;
        this.betaM = betaM;
        this.betaS = betaS;

        this.mu = new double[values.length][];
        for (int i = 0; i < values.length; i++) {
            mu[i] = new double[values[i].length];
        }

        this.sigma = new double[values.length][];
        for (int i = 0; i < values.length; i++) {
            sigma[i] = new double[values[i].length];
            Arrays.fill(sigma[i], 1.0);
        }
    }

    @Override
    public void accept(double[][] delta) {
        synchronized (values) {
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[i].length; j++) {
                    mu[i][j] = betaM * mu[i][j] + (1 - betaM) * delta[i][j];
                    sigma[i][j] = betaS * sigma[i][j] + (1 - betaS) * delta[i][j] * delta[i][j];
                    values[i][j] -= learningRate * mu[i][j] / (Math.sqrt(sigma[i][j]) + 1e-8);
                }
            }
        }
    }
}
