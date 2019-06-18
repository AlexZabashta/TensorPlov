package dataset;

import org.apache.commons.lang3.tuple.Pair;

import core.Result;
import core.VarDiffStruct;

public class ImgConvolution extends Convolution {

    public ImgConvolution(int rowsInp, int colsInp, int rowsOut, int colsOut, VarDiffStruct<double[], double[]> horzFold, VarDiffStruct<double[], double[]> vertFold) {
        super(rowsInp, colsInp, rowsOut, colsOut, horzFold, vertFold);
    }

    @Override
    Result<Pair<double[][][], double[][]>, double[][][]> result(Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar) {
        int curRows = rowsInp, curCols = colsInp;

        while (curRows > rowsOut || curCols > colsOut) {
            double rowCos = -1, colCos = -1;
            int rowF = 0, colF = 0;

            for (int rowU = 1; curRows > rowsOut && rowU < curRows; rowU++) {
                int rowD = rowU - 1;
                double cos = 0;
                for (int col = 0; col < curCols; col++) {
                    for (int i = 0; i < depth; i++) {
                        double valD = (nodes[rowD][col]).values[i];
                        double valU = (nodes[rowU][col]).values[i];
                        cos += valD * valU;
                    }
                }

                cos = Math.abs(cos);

                if (cos > rowCos) {
                    rowCos = cos;
                    rowF = rowD;
                }
            }

            for (int colR = 1; curCols > colsOut && colR < curCols; colR++) {

                int colL = colR - 1;
                double cos = 0;

                for (int row = 0; row < curRows; row++) {
                    for (int i = 0; i < depth; i++) {
                        double valL = (nodes[row][colL]).values[i];
                        double valR = (nodes[row][colR]).values[i];
                        cos += valL * valR;
                    }
                }

                cos = Math.abs(cos);

                if (cos > colCos) {
                    colCos = cos;
                    colF = colL;
                }
            }

            if (rowCos < colCos) {
                --curCols;
                int colS = colF + 1;
                for (int row = 0; row < curRows; row++) {
                    Node node = buildNode(true, horzBoundVar, nodes[row][colF], nodes[row][colS]);
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

                    nodes[rowF][col] = node;
                    for (int row = rowS; row < curRows; row++) {
                        nodes[row][col] = nodes[row + 1][col];
                    }

                }
            }
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
