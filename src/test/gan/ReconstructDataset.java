package test.gan;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import core.DiffFunct;
import core.Result;

public class ReconstructDataset implements DiffFunct<double[][][], double[][]> {

    public final int rows, cols;

    public ReconstructDataset(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Result<double[][][], double[][]> result(double[][][] input) {

        double[] label = new double[rows];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                label[row] += input[row][col][1];
            }
        }

        Integer[] order = new Integer[rows];
        for (int row = 0; row < rows; row++) {
            order[row] = row;
        }
        Arrays.sort(order, Comparator.comparingDouble(i -> label[i]));

        double[][] dataset = new double[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                dataset[row][col] = input[order[row]][col][0];
            }
        }

        double[] mean = new double[cols];
        double[] sigma = new double[cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                mean[col] += dataset[row][col];
            }
        }
        for (int col = 0; col < cols; col++) {
            mean[col] /= rows;
        }

        for (int col = 0; col < cols; col++) {
            double var = 0;
            for (int row = 0; row < rows; row++) {
                dataset[row][col] -= mean[col];
                var += dataset[row][col] * dataset[row][col];
            }

            if (var < 1e-6) {
                sigma[col] = 1;
            } else {
                sigma[col] = 1 / Math.sqrt(var / rows);
            }

            for (int row = 0; row < rows; row++) {
                dataset[row][col] *= sigma[col];
            }
        }

        return new Result<double[][][], double[][]>(new Function<double[][], double[][][]>() {

            @Override
            public double[][][] apply(double[][] deltaDataset) {
                double[][][] deltaObj = new double[rows][cols][2];

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        deltaObj[order[row]][col][0] = deltaDataset[row][col] * sigma[col];

                        if (row * 2 < rows) { // label = -1
                            deltaObj[order[row]][col][1] = input[order[row]][col][1] + 1;
                        } else { // label = +1
                            deltaObj[order[row]][col][1] = input[order[row]][col][1] - 1;
                        }
                    }
                }

                return deltaObj;
            }
        }, dataset);
    }

}
