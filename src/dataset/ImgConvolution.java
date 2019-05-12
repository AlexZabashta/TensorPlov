package dataset;

import org.apache.commons.lang3.tuple.Pair;

import core.VarDiffStruct;

public class ImgConvolution<H, V> extends Convolution<H, V> {

    public ImgConvolution(int depth, VarDiffStruct<double[], H, double[]> horzFold, VarDiffStruct<double[], V, double[]> vertFold) {
        super(depth, horzFold, vertFold);
    }

    @Override
    Pair<Memory, double[]> forward(int rows, int cols, Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar) {
        int curRows = rows, curCols = cols;
        double normH = 0, normV = 0;

        while (curRows > 1 || curCols > 1) {
            double rowSim = Double.POSITIVE_INFINITY, colSim = Double.POSITIVE_INFINITY;
            int rowF = 0, colF = 0;

            for (int rowU = 1; rowU < curRows; rowU++) {
                int rowD = rowU - 1;
                double sim = 0;
                for (int col = 0; col < curCols; col++) {
                    for (int i = 0; i < depth; i++) {
                        double valD = (nodes[rowD][col]).values[i];
                        double valU = (nodes[rowU][col]).values[i];
                        double diff = valD - valU;
                        sim += diff * diff;
                    }
                }

                sim /= curCols;

                if (sim < rowSim) {
                    rowSim = sim;
                    rowF = rowD;
                }
            }

            for (int colR = 1; colR < curCols; colR++) {

                int colL = colR - 1;
                double sim = 0;
                for (int row = 0; row < curRows; row++) {
                    for (int i = 0; i < depth; i++) {
                        double valL = (nodes[row][colL]).values[i];
                        double valR = (nodes[row][colR]).values[i];

                        double diff = valL - valR;
                        sim += diff * diff;

                    }
                }

                sim /= curRows;

                if (sim < colSim) {
                    colSim = sim;
                    colF = colL;
                }
            }

            if (colSim < rowSim) {
                --curCols;
                int colS = colF + 1;
                for (int row = 0; row < curRows; row++) {
                    Node node = buildNode(true, horzBoundVar, nodes[row][colF], nodes[row][colS]);
                    normH += node.weight();

                    nodes[row][colF] = node;
                    for (int col = colS; col < curCols; col++) {
                        nodes[row][col] = nodes[row][col + 1];
                    }
                }
            } else {
                --curRows;

                int rowS = rowF + 1;

                for (int col = 0; col < curCols; col++) {
                    Node node = buildNode(false, vertBoundVar, nodes[rowF][col], nodes[rowS][col]);
                    normV += node.weight();

                    nodes[rowF][col] = node;
                    for (int row = rowS; row < curRows; row++) {
                        nodes[row][col] = nodes[row + 1][col];
                    }
                }
            }
        }

        Node root = nodes[0][0];
        return Pair.of(new Memory(root, rows, cols, normH, normV), root.values);

    }

}
