package dataset;

import java.lang.reflect.Array;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffStruct;
import core.VarDiffStruct;

public abstract class Convolution<H, V> implements DiffStruct<Triple<double[][][], double[], double[]>, Convolution<H, V>.Memory, double[]> {

    public class Memory {
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

        Triple<double[][][], double[], double[]> backward(double[] dy) {
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

    abstract class Fold extends Node {
        final Node first, secnd;

        Fold(Node first, Node secnd, double[] values) {
            super(Math.max(first.level, secnd.level) + 1, values);
            this.first = first;
            this.secnd = secnd;
        }

        void addWithWeightAndNormalization(double[] src, double[] dst) {
            double sum = 1e-9;
            for (int i = 0; i < src.length; i++) {
                sum += src[i] * src[i];
            }
//            double scale = weight() / Math.sqrt(sum);
            double scale = weight();
            for (int i = 0; i < src.length; i++) {
                dst[i] += src[i] * scale;
            }
        }

        abstract double[] backward(double[] dy, double[] sdh, double[] sdv);

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

    class HorzFold extends Fold {
        final H horzMem;

        HorzFold(Node first, Node secnd, H horzMem, double[] values) {
            super(first, secnd, values);
            this.horzMem = horzMem;
        }

        @Override
        double[] backward(double[] dy, double[] sdh, double[] sdv) {
            Pair<double[], double[]> dxh = horzFold.backward(horzMem, dy);
            addWithWeightAndNormalization(sdh, dxh.getRight());
            return dxh.getLeft();
        }
    }

    class VertFold extends Fold {
        final V vertMem;

        VertFold(Node first, Node secnd, V vertMem, double[] values) {
            super(first, secnd, values);
            this.vertMem = vertMem;
        }

        @Override
        double[] backward(double[] dy, double[] sdh, double[] sdv) {
            Pair<double[], double[]> dxv = vertFold.backward(vertMem, dy);
            addWithWeightAndNormalization(sdv, dxv.getRight());
            return dxv.getLeft();
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
    public final VarDiffStruct<double[], H, double[]> horzFold;
    public final VarDiffStruct<double[], V, double[]> vertFold;

    public Convolution(int depth, VarDiffStruct<double[], H, double[]> horzFold, VarDiffStruct<double[], V, double[]> vertFold) {
        this.depth = depth;
        this.horzFold = horzFold;
        this.vertFold = vertFold;
    }

    @Override
    public Triple<double[][][], double[], double[]> backward(Memory memory, double[] deltaOutput) {
        return memory.backward(deltaOutput);
    }

    Fold buildNode(boolean horizontal, double[] foldBoundVar, Node first, Node secnd) {
        double[] x = new double[depth * 2];
        System.arraycopy(first.values, 0, x, 0, depth);
        System.arraycopy(secnd.values, 0, x, depth, depth);

        if (horizontal) {
            Pair<H, double[]> result = horzFold.forward(x, foldBoundVar);
            return new HorzFold(first, secnd, result.getLeft(), result.getRight());
        } else {
            Pair<V, double[]> result = vertFold.forward(x, foldBoundVar);
            return new VertFold(first, secnd, result.getLeft(), result.getRight());
        }
    }

    @Override
    public Pair<Memory, double[]> forward(Triple<double[][][], double[], double[]> input) {
        return forward(input.getLeft(), input.getMiddle(), input.getRight());
    }

    public Pair<Memory, double[]> forward(double[][][] dataset, double[] horzBoundVar, double[] vertBoundVar) {
        final int rows = dataset.length, cols = dataset[0].length;

        @SuppressWarnings("unchecked")
        Node[][] nodes = (Node[][]) Array.newInstance(Node.class, rows, cols);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                nodes[row][col] = new DatasetCell(dataset[row][col], row, col);
            }
        }
        return forward(rows, cols, nodes, horzBoundVar, vertBoundVar);
    }

    abstract Pair<Memory, double[]> forward(int rows, int cols, Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar);

    @SuppressWarnings("unchecked")
    @Override
    public Class<Triple<double[][][], double[], double[]>> inputClass() {
        return (Class<Triple<double[][][], double[], double[]>>) Triple.of(null, null, null).getClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Memory> memoryClass() {
        return (Class<Memory>) new Memory(null, 0, 0, 0, 0).getClass();
    }

}
