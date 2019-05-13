package dataset;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffFunct;
import core.Result;
import core.VarDiffStruct;

public abstract class Convolution implements DiffFunct<Triple<double[][][], double[], double[]>, double[]> {

    public class Memory implements Function<double[], Triple<double[][][], double[], double[]>> {
        final Node root;
        final int rows, cols;
        final double normH, normV;

        Memory(Node root, int rows, int cols, double normH, double normV) {
            this.root = root;
            this.rows = rows;
            this.cols = cols;
            this.normH = normH;
            this.normV = normV;
        }

        void normalize(double[] array, double scale) {
            for (int i = 0; i < array.length; i++) {
                array[i] *= scale;
            }
        }

        @Override
        public Triple<double[][][], double[], double[]> apply(double[] dy) {
            double[][][] dx = new double[rows][cols][];
            double[] dh = new double[horzFold.numBoundVars()];
            double[] dv = new double[vertFold.numBoundVars()];

            root.backward(dy, dx, dh, dv);

            if (normH > 0) {
                normalize(dh, 1.0 / normH);
            }

            if (normV > 0) {
                normalize(dv, 1.0 / normV);
            }

            return Triple.of(dx, dh, dv);
        }

    }

    abstract class Node {
        final double[] values;
        final int level;

        Node(int level, double[] values) {
            this.level = level;
            this.values = values;
        }

        abstract void backward(double[] dy, double[][][] dx, double[] sdh, double[] sdv);

        double weight() {
            return level;
        }

    }

    class Fold extends Node {
        final Node first, secnd;
        final Function<double[], Pair<double[], double[]>> derivative;

        Fold(Node first, Node secnd, Function<double[], Pair<double[], double[]>> derivative, double[] values) {
            super(Math.max(first.level, secnd.level) + 1, values);
            this.first = first;
            this.secnd = secnd;
            this.derivative = derivative;
        }

        void addWithWeightAndNormalization(double[] src, double[] dst) {
            double sum = 1e-9;
            for (int i = 0; i < src.length; i++) {
                sum += src[i] * src[i];
            }
            // double scale = weight() / Math.sqrt(sum);
            double scale = weight();
            for (int i = 0; i < src.length; i++) {
                dst[i] += src[i] * scale;
            }
        }

        double[] backward(double[] dy, double[] sdh, double[] sdv) {
            Pair<double[], double[]> dxh = derivative.apply(dy);
            addWithWeightAndNormalization(sdh, dxh.getRight());
            return dxh.getLeft();
        }

        @Override
        void backward(double[] dy, double[][][] dInput, double[] sdh, double[] sdv) {
            double[] dx = backward(dy, sdh, sdv);

            double[] da = new double[depth];
            double[] db = new double[depth];

            System.arraycopy(dx, 0, da, 0, depth);
            System.arraycopy(dx, depth, db, 0, depth);

            first.backward(da, dInput, sdh, sdv);
            secnd.backward(db, dInput, sdh, sdv);
        }

    }

    class DatasetCell extends Node {
        final int row, col;

        DatasetCell(double[] values, int row, int col) {
            super(1, values);
            this.row = row;
            this.col = col;
        }

        @Override
        void backward(double[] dy, double[][][] dx, double[] sdh, double[] sdv) {
            dx[row][col] = dy;
        }

    }

    public final int depth;
    public final VarDiffStruct<double[], double[]> horzFold;
    public final VarDiffStruct<double[], double[]> vertFold;

    public Convolution(int depth, VarDiffStruct<double[], double[]> horzFold, VarDiffStruct<double[], double[]> vertFold) {
        this.depth = depth;
        this.horzFold = horzFold;
        this.vertFold = vertFold;
    }

    Fold buildNode(boolean horizontal, double[] foldBoundVar, Node first, Node secnd) {
        double[] x = new double[depth * 2];
        System.arraycopy(first.values, 0, x, 0, depth);
        System.arraycopy(secnd.values, 0, x, depth, depth);

        if (horizontal) {
            Result<Pair<double[], double[]>, double[]> result = horzFold.result(x, foldBoundVar);
            return new Fold(first, secnd, result.derivative(), result.value());
        } else {
            Result<Pair<double[], double[]>, double[]> result = vertFold.result(x, foldBoundVar);
            return new Fold(first, secnd, result.derivative(), result.value());
        }
    }

    @Override
    public Result<Triple<double[][][], double[], double[]>, double[]> result(Triple<double[][][], double[], double[]> input) {
        return result(input.getLeft(), input.getMiddle(), input.getRight());
    }

    public Result<Triple<double[][][], double[], double[]>, double[]> result(double[][][] dataset, double[] horzBoundVar, double[] vertBoundVar) {
        final int rows = dataset.length, cols = dataset[0].length;

        Node[][] nodes = new Node[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                nodes[row][col] = new DatasetCell(dataset[row][col], row, col);
            }
        }
        return result(rows, cols, nodes, horzBoundVar, vertBoundVar);
    }

    abstract Result<Triple<double[][][], double[], double[]>, double[]> result(int rows, int cols, Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar);

}
