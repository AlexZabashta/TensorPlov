package dataset;

import java.util.Arrays;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffFunct;
import core.Result;
import core.VarDiffStruct;

public class Deconvolution implements DiffFunct<Triple<double[], double[], double[]>, double[][][]> {

    public final int depth;
    public final VarDiffStruct<double[], double[]> horzExpand;
    public final VarDiffStruct<double[], double[]> vertExpand;
    public final int rows, cols;

    public static final int ROW_EXP_ID = 0;
    public static final int COL_EXP_ID = 1;

    public Deconvolution(int depth, VarDiffStruct<double[], double[]> horzExpand, VarDiffStruct<double[], double[]> vertExpand, int rows, int cols) {
        this.depth = depth;
        this.horzExpand = horzExpand;
        this.vertExpand = vertExpand;
        this.rows = rows;
        this.cols = cols;
    }

    class Node {
        double[] values;
        int level;
        Node first, secnd;

        double[] delta;
        boolean horizontal;

        double[] delta(int maxLevel, double[] sdh, double[] sdv) {
            if (delta == null) {
                double[] firstDelta = first.delta(maxLevel, sdh, sdv);
                double[] secndDelta = secnd.delta(maxLevel, sdh, sdv);

                double[] common = new double[depth * 2];

                System.arraycopy(firstDelta, 0, common, 0, depth);
                System.arraycopy(secndDelta, 0, common, depth, depth);

                Pair<double[], double[]> dxe = derivative.apply(common);

                double[] src = dxe.getRight();
                double[] dst = horizontal ? sdh : sdv;

                for (int i = 0; i < src.length; i++) {
                    dst[i] += src[i];
                }
                delta = dxe.getLeft();

                double expand = (maxLevel - level) * 1.0 / maxLevel;

                delta[ROW_EXP_ID] = delta[COL_EXP_ID] = expand;
            }

            return delta;
        }

        Function<double[], Pair<double[], double[]>> derivative;

        Node(double[] values) {
            this.level = 1;
            this.values = values;
        }

        double weight() {
            return level;
        }

        public Node() {
        }

        void expand(boolean horizontal, double[] horzBoundVar, double[] vertBoundVar, Node first, Node secnd) {
            (this.first = first).level = level + 1;
            (this.secnd = secnd).level = level + 1;

            this.horizontal = horizontal;

            Result<Pair<double[], double[]>, double[]> result;

            if (horizontal) {
                result = horzExpand.result(values, horzBoundVar);
            } else {
                result = vertExpand.result(values, vertBoundVar);
            }
            derivative = result.derivative();

            double[] firstValues = first.values = new double[depth];
            double[] secndValues = secnd.values = new double[depth];

            System.arraycopy(result.value(), 0, firstValues, 0, depth);
            System.arraycopy(result.value(), depth, secndValues, 0, depth);

        }

    }

    @Override
    public Result<Triple<double[], double[], double[]>, double[][][]> result(Triple<double[], double[], double[]> input) {
        return result(input.getLeft(), input.getMiddle(), input.getRight());
    }

    public Result<Triple<double[], double[], double[]>, double[][][]> result(double[] vector, double[] horzBoundVar, double[] vertBoundVar) {
        final Node[][] nodes = new Node[rows][cols];

        int curRows = 1, curCols = 1;
        double sumHor = 0, sumVer = 0;

        final Node root = new Node(vector);
        nodes[0][0] = root;

        while (curRows < rows || curCols < cols) {
            double expandRow = Double.NEGATIVE_INFINITY, expandCol = Double.NEGATIVE_INFINITY;
            int rowId = 0, colId = 0;

            for (int row = 0; row < curRows && curRows < rows; row++) {
                double sum = 0;
                for (int col = 0; col < curCols; col++) {
                    sum += nodes[row][col].values[ROW_EXP_ID];
                }
                sum /= curCols;
                if (sum > expandRow) {
                    expandRow = sum;
                    rowId = row;
                }
            }

            for (int col = 0; col < curCols && curCols < cols; col++) {
                double sum = 0;
                for (int row = 0; row < curRows; row++) {
                    sum += nodes[row][col].values[COL_EXP_ID];
                }
                sum /= curRows;
                if (sum > expandCol) {
                    expandCol = sum;
                    colId = col;
                }
            }

            if (expandRow < expandCol) {
                for (int row = 0; row < curRows; row++) {
                    Node first = new Node(), secnd = new Node(), current = nodes[row][colId];

                    current.expand(true, horzBoundVar, vertBoundVar, first, secnd);
                    sumHor += current.weight();

                    nodes[row][colId] = first;
                    nodes[row][curCols] = secnd;
                }
                ++curCols;
            } else {
                for (int col = 0; col < curCols; col++) {
                    Node first = new Node(), secnd = new Node(), current = nodes[rowId][col];

                    current.expand(false, horzBoundVar, vertBoundVar, first, secnd);
                    sumVer += current.weight();

                    nodes[rowId][col] = first;
                    nodes[curRows][col] = secnd;
                }
                ++curRows;
            }
        }

        final double normHor = 1 / sumHor, normVer = 1 / sumVer;

        double[][][] dataset = new double[rows][cols][];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                dataset[row][col] = nodes[row][col].values;
            }
        }

        return new Result<Triple<double[], double[], double[]>, double[][][]>(new Function<double[][][], Triple<double[], double[], double[]>>() {

            @Override
            public Triple<double[], double[], double[]> apply(double[][][] deltaDataset) {
                double[] deltaHorzBoundVar = new double[horzExpand.numBoundVars()];
                double[] deltaVertBoundVar = new double[vertExpand.numBoundVars()];

                int maxLevel = 0;

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        maxLevel = Math.max(nodes[row][col].level, maxLevel);
                        nodes[row][col].delta = deltaDataset[row][col]; // EDIT DELTA
                    }
                }

                double[] delta = root.delta(maxLevel, deltaHorzBoundVar, deltaVertBoundVar);

                for (int i = 0; i < deltaHorzBoundVar.length; i++) {
                    deltaHorzBoundVar[i] *= normHor;
                }

                for (int i = 0; i < deltaVertBoundVar.length; i++) {
                    deltaVertBoundVar[i] *= normVer;
                }

                return Triple.of(delta, deltaHorzBoundVar, deltaVertBoundVar);
            }

        }, dataset);

    }

}
