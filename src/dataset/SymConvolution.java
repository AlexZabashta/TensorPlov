package dataset;

import org.apache.commons.lang3.tuple.Pair;

import core.VarDiffStruct;

public class SymConvolution<H, V> extends Convolution<H, V> {

    public SymConvolution(int depth, VarDiffStruct<double[], H, double[]> horzFold, VarDiffStruct<double[], V, double[]> vertFold) {
        super(depth, horzFold, vertFold);
    }

    @Override
    Pair<Memory, double[]> forward(int rows, int cols, Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar) {
        int curRows = rows, curCols = cols;
        double normH = 0, normV = 0;

        while (curRows > 1 || curCols > 1) {
            double rowSim = Double.POSITIVE_INFINITY, colSim = Double.POSITIVE_INFINITY;
            int rowA = 0, rowB = 1;
            int colA = 0, colB = 1;

            for (int rowU = 1; rowU < curRows; rowU++) {
                for (int rowD = 0; rowD < rowU; rowD++) {
                    double sim = 0;
                    double sumD = 0, sumU = 0;
                    for (int col = 0; col < curCols; col++) {
                        for (int i = 0; i < depth; i++) {
                            double valD = (nodes[rowD][col]).values[i];
                            double valU = (nodes[rowU][col]).values[i];

                            sumD += valD;
                            sumU += valU;
                            double diff = valD - valU;
                            sim += diff * diff;
                        }
                    }

                    sim /= curCols;

                    if (sim < rowSim) {
                        rowSim = sim;
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
                    double sim = 0;
                    double sumL = 0, sumR = 0;
                    for (int row = 0; row < curRows; row++) {
                        for (int i = 0; i < depth; i++) {
                            double valL = (nodes[row][colL]).values[i];
                            double valR = (nodes[row][colR]).values[i];

                            sumL += valL;
                            sumR += valR;
                            double diff = valL - valR;
                            sim += diff * diff;

                        }
                    }

                    sim /= curRows;

                    if (sim < colSim) {
                        colSim = sim;
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

            if (colSim < rowSim) {
                --curCols;
                for (int row = 0; row < curRows; row++) {
                    Node node = buildNode(true, horzBoundVar, nodes[row][colA], nodes[row][colB]);
                    nodes[row][colA] = node;
                    nodes[row][colB] = nodes[row][curCols];
                    normH += node.weight();
                }
            } else {
                --curRows;

                for (int col = 0; col < curCols; col++) {
                    Node node = buildNode(false, vertBoundVar, nodes[rowA][col], nodes[rowB][col]);
                    nodes[rowA][col] = node;
                    nodes[rowB][col] = nodes[curRows][col];
                    normV += node.weight();
                }
            }
        }

        Node root = nodes[0][0];
        return Pair.of(new Memory(root, rows, cols, normH, normV), root.values);

    }

}
