package grad;

import java.util.Arrays;
import java.util.function.Consumer;

public class AdaGrad implements Consumer<double[]> {
    final double[] values;
    final double[] mu, sigma;
    final double betaM, betaS;
    final double learningRate;

    public AdaGrad(double[] values, double learningRate, double betaM, double betaS) {
        this.values = values;
        this.learningRate = learningRate;
        this.betaM = betaM;
        this.betaS = betaS;

        this.mu = new double[values.length];
        this.sigma = new double[values.length];
        Arrays.fill(sigma, 1.0);
    }

    @Override
    public void accept(double[] delta) {
        synchronized (values) {
            for (int i = 0; i < values.length; i++) {
                mu[i] = betaM * mu[i] + (1 - betaM) * delta[i];
                sigma[i] = betaS * sigma[i] + (1 - betaS) * delta[i] * delta[i];
                values[i] -= learningRate * mu[i] / (Math.sqrt(sigma[i]) + 1e-8);
            }
        }
    }

}
