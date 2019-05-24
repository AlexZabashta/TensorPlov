package dataset;

import org.apache.commons.lang3.tuple.Pair;

import core.Result;
import core.VarDiffStruct;

public class SymConvolution extends Convolution {
    public SymConvolution(int rows, int cols, VarDiffStruct<double[], double[]> horzFold, VarDiffStruct<double[], double[]> vertFold) {
        super(rows, cols, horzFold, vertFold);
    }

    @Override
    Result<Pair<double[][][], double[][]>, double[]> result(Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar) {
        int curRows = rows, curCols = cols;

        // long searchTime = 0;
        // long convTime = 0;
        // long time1, time2, time3;

        while (curRows > 1 || curCols > 1) {
            double rowCos = -1, colCos = -1;
            int rowA = 0, rowB = 1;
            int colA = 0, colB = 1;

            // time1 = System.currentTimeMillis();

            for (int rowU = 1; rowU < curRows; rowU++) {
                for (int rowD = 0; rowD < rowU; rowD++) {
                    double cos = 0;
                    double sumD = 0, sumU = 0;
                    for (int col = 0; col < curCols; col++) {
                        for (int i = 0; i < depth / 2; i++) {
                            double valD = (nodes[rowD][col]).values[i];
                            double valU = (nodes[rowU][col]).values[i];

                            sumD += valD;
                            sumU += valU;
                            cos += valD * valU;
                        }
                    }

                    cos = Math.abs(cos);

                    if (cos > rowCos) {
                        rowCos = cos;
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

            for (int colR = 1; colR < curCols; colR++) {
                for (int colL = 0; colL < colR; colL++) {
                    double cos = 0;
                    double sumL = 0, sumR = 0;
                    for (int row = 0; row < curRows; row++) {
                        for (int i = 0; i < depth / 2; i++) {
                            double valL = (nodes[row][colL]).values[i];
                            double valR = (nodes[row][colR]).values[i];

                            sumL += valL;
                            sumR += valR;
                            cos += valL * valR;

                        }
                    }

                    cos = Math.abs(cos);

                    if (cos > colCos) {
                        colCos = cos;
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

            if (colCos > rowCos) {
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

        Node root = nodes[0][0];
        // System.err.println(rows + " " + cols + " " + searchTime + " " + convTime); // + " " + Arrays.toString(root.values)

        return new Result<>(new Memory(root), root.values);

    }

    @Override
    public String toString() {
        return "SymConvolution [rows=" + rows + ", cols=" + cols + ", depth=" + depth + ", horzFold=" + horzFold + ", vertFold=" + vertFold + "]";
    }

}
