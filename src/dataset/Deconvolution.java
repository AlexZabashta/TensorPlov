package dataset;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import core.MultiVarDiffStruct;
import core.Result;
import core.VarDiffStruct;

public class Deconvolution implements MultiVarDiffStruct<double[][][], double[]> {

    class Node {
        double[] values;

        int level;
        Node first, secnd;

        boolean horizontal;
        Function<double[], Pair<double[], double[]>> derivative;

        double[] delta;
        double error;

        public Node() {
        }

        Node(double[] values) {
            this.level = 1;
            this.values = values;
        }

        double[] delta(int maxLevel, double minError, double maxError, double avgError, double[] sdh, double[] sdv) {
            if (delta == null) {
                double[] firstDelta = first.delta(maxLevel, minError, maxError, avgError, sdh, sdv);
                double[] secndDelta = secnd.delta(maxLevel, minError, maxError, avgError, sdh, sdv);

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
                error = (first.error + secnd.error) / 2;

                double defaultExpand = (maxLevel - level) * 1.0 / maxLevel;
                double errorExpand = avgError - error;
                if (errorExpand > 0) {
                    errorExpand /= avgError - minError;
                } else {
                    errorExpand /= maxError - avgError;
                }

                if (horizontal) { // => expanded columns
                    delta[ROW_EXP_ID] = values[ROW_EXP_ID] - defaultExpand;
                    delta[COL_EXP_ID] = values[COL_EXP_ID] - errorExpand;
                } else { // => expanded rows
                    delta[ROW_EXP_ID] = values[ROW_EXP_ID] - errorExpand;
                    delta[COL_EXP_ID] = values[COL_EXP_ID] - defaultExpand;
                }

                delta[ROW_EXP_ID] = delta[COL_EXP_ID] = defaultExpand;
            }

            if (derivative == null) {
                double errorExpand = avgError - error;
                if (errorExpand > 0) {
                    errorExpand /= avgError - minError;
                } else {
                    errorExpand /= maxError - avgError;
                }

                delta[ROW_EXP_ID] = values[ROW_EXP_ID] - errorExpand;
                delta[COL_EXP_ID] = values[COL_EXP_ID] - errorExpand;
            }

            return delta;
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

        void setDelta(double[] delta) {
            this.delta = delta;
            double sum = 0;
            for (int i = 0; i < depth; i++) {
                if (i == ROW_EXP_ID || i == COL_EXP_ID) {
                    continue;
                }
                sum += delta[i] * delta[i];
            }
            error = Math.sqrt(sum);
        }

        double weight() {
            return level;
        }

    }

    public static final int COL_EXP_ID = 1;
    public static final int ROW_EXP_ID = 0;
    public final int depth;

    public final VarDiffStruct<double[], double[]> horzExpand;
    public final int rows, cols;

    public final VarDiffStruct<double[], double[]> vertExpand;

    public Deconvolution(int depth, VarDiffStruct<double[], double[]> horzExpand, VarDiffStruct<double[], double[]> vertExpand, int rows, int cols) {
        this.depth = depth;
        this.horzExpand = horzExpand;
        this.vertExpand = vertExpand;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Result<Pair<double[][][], double[][]>, double[]> result(double[][][] freeVar, double[]... bounVar) {
        return result(freeVar, bounVar[0], bounVar[1]);
    }

    public Result<Pair<double[], double[][]>, double[][][]> result(double[] vector, double[] horzBoundVar, double[] vertBoundVar) {
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

        return new Result<Pair<double[], double[][]>, double[][][]>(new Function<double[][][], Pair<double[], double[][]>>() {

            @Override
            public Pair<double[], double[][]> apply(double[][][] deltaDataset) {
                double[] deltaHorzBoundVar = new double[horzExpand.numBoundVars()];
                double[] deltaVertBoundVar = new double[vertExpand.numBoundVars()];

                int maxLevel = 0;

                double minError = Double.POSITIVE_INFINITY;
                double maxError = Double.NEGATIVE_INFINITY;
                double avgError = 0;
                double nrmError = 0;

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        Node node = nodes[row][col];

                        maxLevel = Math.max(node.level, maxLevel);
                        node.setDelta(deltaDataset[row][col]);

                        minError = Math.min(minError, node.error);
                        maxError = Math.max(maxError, node.error);
                        avgError = node.error * node.weight();
                        nrmError = node.weight();
                    }
                }

                double[] delta = root.delta(maxLevel, minError - 1e-3, maxError + 1e-3, avgError / nrmError, deltaHorzBoundVar, deltaVertBoundVar);

                for (int i = 0; i < deltaHorzBoundVar.length; i++) {
                    deltaHorzBoundVar[i] *= normHor;
                }

                for (int i = 0; i < deltaVertBoundVar.length; i++) {
                    deltaVertBoundVar[i] *= normVer;
                }

                return Pair.of(delta, new double[][] { deltaHorzBoundVar, deltaVertBoundVar });

            }

        }, dataset);

    }

    @Override
    public int[] numBoundVars() {
        return new int[] { horzExpand.numBoundVars(), vertExpand.numBoundVars() };
    }

}
