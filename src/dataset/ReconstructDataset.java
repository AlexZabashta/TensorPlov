package dataset;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import core.DiffFunct;
import core.Result;
import core.TensorShape;

public class ReconstructDataset implements DiffFunct<double[][][], double[][][]> {
    public final int rows, cols;

    public ReconstructDataset(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public TensorShape inputType() {
        return new TensorShape(rows, cols, 2);
    }

    @Override
    public TensorShape outputType() {
        return new TensorShape(rows, cols, 2);
    }

    @Override
    public Result<double[][][], double[][][]> result(double[][][] input) {

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
                var += v * v;
                skw += v * v * v;
            }
            if (var > 1e-7) {
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

            for (int row = 0; row < rows; row++) {
                dataset[row][col][1] = (2 * row < rows) ? -1.0 : +1.0;
            }
        }

        return new Result<double[][][], double[][][]>(new Function<double[][][], double[][][]>() {

            @Override
            public double[][][] apply(double[][][] delta) {
                double[][][] ordDelta = new double[rows][cols][2];

                for (int col = 0; col < cols; col++) {
                    for (int row = 0; row < rows; row++) {
                        ordDelta[row][col][0] = delta[order[row]][col][0] * sigma[col];
                        ordDelta[row][col][1] = 2 * (input[row][col][1] - dataset[row][col][1]) + delta[order[row]][col][1];
                    }
                }

                return ordDelta;
            }
        }, dataset);
    }

}
