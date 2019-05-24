package dataset;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import core.BoundVarShape;
import core.MultiVarDiffStruct;
import core.Result;
import core.TensorShape;
import core.VarDiffStruct;
import core.VectorShape;

public class Deconvolution implements MultiVarDiffStruct<double[], double[][][]> {

    class Node {
        double[] delta;

        Function<double[], Pair<double[], double[]>> derivative;
        double error;
        Node first, secnd;

        boolean horizontal;
        int level, size = 1;

        double sumHor = 0, sumVer = 0;
        double[] values;

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

                sumHor += first.sumHor;
                sumHor += secnd.sumHor;

                sumHor += first.sumVer;
                sumVer += secnd.sumVer;

                double[] common = new double[depth * 2];

                System.arraycopy(firstDelta, 0, common, 0, depth);
                System.arraycopy(secndDelta, 0, common, depth, depth);

                Pair<double[], double[]> dxe = derivative.apply(common);

                double[] src = dxe.getRight();
                double[] dst = horizontal ? sdh : sdv;

                double scale = weight();
                for (int i = 0; i < src.length; i++) {
                    dst[i] += src[i] * scale;
                }

                if (horizontal) {
                    sumHor += scale;
                } else {
                    sumVer += scale;
                }

                delta = Arrays.copyOfRange(dxe.getLeft(), 4, depth + 4);
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

            Random random = new Random();

            double[] evalues = new double[depth + 4];
            System.arraycopy(values, 0, evalues, 4, depth);
            for (int i = 0; i < 4; i++) {
                evalues[i] += random.nextGaussian();
            }

            if (horizontal) {
                result = horzExpand.result(evalues, horzBoundVar);
            } else {
                result = vertExpand.result(evalues, vertBoundVar);
            }
            derivative = result.derivative();

            double[] firstValues = first.values = new double[depth];
            double[] secndValues = secnd.values = new double[depth];

            System.arraycopy(result.value(), 0, firstValues, 0, depth);
            System.arraycopy(result.value(), depth, secndValues, 0, depth);

        }

        void setDelta(double[] delta) {
            this.delta = delta;
            this.size = 1;
            double sum = 0;
            for (int i = 0; i < depth; i++) {
                if (i == ROW_EXP_ID || i == COL_EXP_ID) {
                    continue;
                }
                sum += delta[i] * delta[i];
            }
            error = Math.sqrt(sum);
        }

        public void setSize() {
            if (first != null && secnd != null) {
                first.setSize();
                secnd.setSize();
                size = first.size + secnd.size;
            }
        }

        double weight() {
            return size;
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
    public BoundVarShape boundVarShape() {
        return new BoundVarShape(horzExpand.numBoundVars(), vertExpand.numBoundVars());
    }

    @Override
    public VectorShape freeVarType() {
        return new VectorShape(depth);
    }

    @Override
    public double[][] genBoundVars() {
        return new double[][] { horzExpand.genBoundVars(), vertExpand.genBoundVars() };
    }

    @Override
    public TensorShape outputType() {
        return new TensorShape(rows, cols, depth);
    }

    @Override
    public Result<Pair<double[], double[][]>, double[][][]> result(double[] freeVar, double[]... bounVar) {
        return result(freeVar, bounVar[0], bounVar[1]);
    }

    public Result<Pair<double[], double[][]>, double[][][]> result(double[] vector, double[] horzBoundVar, double[] vertBoundVar) {
        final Node[][] nodes = new Node[rows][cols];

        int curRows = 1, curCols = 1;

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

                    nodes[row][colId] = first;
                    nodes[row][curCols] = secnd;
                }
                ++curCols;
            } else {
                for (int col = 0; col < curCols; col++) {
                    Node first = new Node(), secnd = new Node(), current = nodes[rowId][col];

                    current.expand(false, horzBoundVar, vertBoundVar, first, secnd);

                    nodes[rowId][col] = first;
                    nodes[curRows][col] = secnd;
                }
                ++curRows;
            }
        }

        double[][][] dataset = new double[rows][cols][];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                dataset[row][col] = nodes[row][col].values;
            }
        }
        root.setSize();

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
                        avgError += node.error * node.weight();
                        nrmError += node.weight();
                    }
                }

                double[] delta = root.delta(maxLevel, minError - 1e-3, maxError + 1e-3, avgError / nrmError, deltaHorzBoundVar, deltaVertBoundVar);

                double normHor = 1 / root.sumHor;
                double normVer = 1 / root.sumVer;

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

}
