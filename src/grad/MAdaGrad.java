package grad;

import java.util.function.Consumer;

public class MAdaGrad implements Consumer<double[][]> {
    final double[][] values;
    final double[][] mean, vari;
    final double betaM, betaV;
    final double learningRate;

    double betaMt = 1, betaVt = 1;

    public MAdaGrad(double[][] values, double learningRate, double betaM, double betaV) {
        this.values = values;
        this.learningRate = learningRate;
        this.betaM = betaM;
        this.betaV = betaV;

        this.mean = new double[values.length][];
        for (int i = 0; i < values.length; i++) {
            mean[i] = new double[values[i].length];
        }

        this.vari = new double[values.length][];
        for (int i = 0; i < values.length; i++) {
            vari[i] = new double[values[i].length];
        }
    }

    @Override
    public void accept(double[][] delta) {
        synchronized (values) {

            betaMt *= betaM;
            betaVt *= betaV;

            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[i].length; j++) {
                    mean[i][j] = betaM * mean[i][j] + (1 - betaM) * delta[i][j];
                    vari[i][j] = betaV * vari[i][j] + (1 - betaV) * delta[i][j] * delta[i][j];

                    double correctMean = mean[i][j] / (1 - betaMt);
                    double correctVari = vari[i][j] / (1 - betaVt);

                    values[i][j] -= learningRate * correctMean / (Math.sqrt(correctVari) + 1e-8);
                }
            }
        }
    }
}
