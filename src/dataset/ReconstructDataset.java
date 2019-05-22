package dataset;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import core.DiffFunct;
import core.Result;

public class ReconstructDataset implements DiffFunct<double[][][], double[][][]> {

    @Override
    public Result<double[][][], double[][][]> result(double[][][] input) {
        int rows = input.length;
        int cols = input[0].length;

        double[] mean = new double[cols];

        double[][][] dataset = new double[rows][cols][2];

        Integer[] order = new Integer[rows];
        for (int row = 0; row < rows; row++) {
            order[row] = row;
        }

        double[] sum = new double[rows];
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                mean[col] += input[row][col][0];
                sum[row] += input[row][col][1];
            }
            mean[col] /= rows;
        }

        Arrays.sort(order, Comparator.comparingDouble(i -> sum[i]));

        double[] sigma = new double[cols];
        for (int col = 0; col < cols; col++) {
            double var = 0, skw = 0;
            for (int row = 0; row < rows; row++) {
                double v = dataset[order[row]][col][0] = input[row][col][0] - mean[col];
                dataset[order[row]][col][1] = input[row][col][1];
                var += v * v;
                skw += v * v * v;
            }
            if (var < 1e-9) {
                sigma[col] = 1 / Math.sqrt(var / rows);
            } else {
                sigma[col] = 1;
            }
            if (skw < 0) {
                sigma[col] *= -1;
            }

            for (int row = 0; row < rows; row++) {
                dataset[row][col][0] *= sigma[col];
            }
        }

        return new Result<double[][][], double[][][]>(new Function<double[][][], double[][][]>() {

            @Override
            public double[][][] apply(double[][][] delta) {
                double[][][] ordDelta = new double[rows][cols][2];

                for (int col = 0; col < cols; col++) {
                    for (int row = 0; row < rows; row++) {
                        ordDelta[row][col][1] = delta[order[row]][col][1];
                        ordDelta[row][col][0] = delta[order[row]][col][0] * sigma[col];
                    }
                }

                return ordDelta;
            }
        }, dataset);
    }

}
