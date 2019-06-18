package dataset;

import org.apache.commons.lang3.tuple.Pair;

import core.Result;
import core.VarDiffStruct;
import dataset.Convolution.Memory;

public class SymConvolution extends Convolution {

    public SymConvolution(int rowsInp, int colsInp, int rowsOut, int colsOut, VarDiffStruct<double[], double[]> horzFold, VarDiffStruct<double[], double[]> vertFold) {
        super(rowsInp, colsInp, rowsOut, colsOut, horzFold, vertFold);
    }

    @Override
    Result<Pair<double[][][], double[][]>, double[][][]> result(Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar) {
        int curRows = rowsInp, curCols = colsInp;

        // long searchTime = 0;
        // long convTime = 0;
        // long time1, time2, time3;

        while (curRows > rowsOut || curCols > colsOut) {
            double rowMSE = Double.POSITIVE_INFINITY, colMSE = Double.POSITIVE_INFINITY;
            int rowA = 0, rowB = 1;
            int colA = 0, colB = 1;

            // time1 = System.currentTimeMillis();

            for (int rowU = 1; curRows > rowsOut && rowU < curRows; rowU++) {
                for (int rowD = 0; rowD < rowU; rowD++) {
                    double mse = 0;
                    double sumD = 0, sumU = 0;
                    for (int col = 0; col < curCols; col++) {
                        for (int i = 0; i < depth; i++) {
                            double valD = (nodes[rowD][col]).values[i];
                            double valU = (nodes[rowU][col]).values[i];

                            sumD += valD;
                            sumU += valU;

                            double diff = valD - valU;
                            mse += diff * diff;
                        }
                    }

                    // cos = Math.abs(cos);

                    if (mse < rowMSE) {
                        rowMSE = mse;
                        rowA = rowD;
                        rowB = rowU;

                        if (sumD > sumU) {
                            rowA ^= rowB;
                            rowB ^= rowA;
                            rowA ^= rowB;
                        }
                    }
                }
            }

            for (int colR = 1; curCols > colsOut && colR < curCols; colR++) {
                for (int colL = 0; colL < colR; colL++) {
                    double mse = 0;
                    double sumL = 0, sumR = 0;
                    for (int row = 0; row < curRows; row++) {
                        for (int i = 0; i < depth; i++) {
                            double valL = (nodes[row][colL]).values[i];
                            double valR = (nodes[row][colR]).values[i];

                            sumL += valL;
                            sumR += valR;

                            double diff = valL - valR;
                            mse += diff * diff;

                            // cos += valL * valR;

                        }
                    }

                    // cos = Math.abs(cos);

                    if (mse < colMSE) {
                        colMSE = mse;
                        colA = colL;
                        colB = colR;

                        if (sumL > sumR) {
                            colA ^= colB;
                            colB ^= colA;
                            colA ^= colB;
                        }
                    }
                }
            }

            // time2 = System.currentTimeMillis();

            // searchTime += time2 - time1;

            if (colMSE * curCols < rowMSE * curRows) {
                --curCols;
                for (int row = 0; row < curRows; row++) {
                    Node node = buildNode(true, horzBoundVar, nodes[row][colA], nodes[row][colB]);
                    nodes[row][colA] = node;
                    nodes[row][colB] = nodes[row][curCols];
                }
            } else {
                --curRows;

                for (int col = 0; col < curCols; col++) {
                    Node node = buildNode(false, vertBoundVar, nodes[rowA][col], nodes[rowB][col]);
                    nodes[rowA][col] = node;
                    nodes[rowB][col] = nodes[curRows][col];
                }
            }

            // time3 = System.currentTimeMillis();

            // convTime += time3 - time2;
        }

        double[][][] output = new double[rowsOut][colsOut][];

        for (int row = 0; row < rowsOut; row++) {
            for (int col = 0; col < colsOut; col++) {
                output[row][col] = nodes[row][col].values;
            }
        }

        return new Result<>(new Memory(nodes), output);

    }

}
