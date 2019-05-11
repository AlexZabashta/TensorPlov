package dataset;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffStruct;

public class Convolution<H, V> implements DiffStruct<Triple<double[][][], double[], double[]>, Convolution<H, V>.Memory, double[]> {

    class Memory {
        final Node root;
        final int rows, columns;
        final int numHorzBoundVar, numVertBoundVar;

        public Memory(Node root, int rows, int columns, int numHorzBoundVar, int numVertBoundVar) {
            this.root = root;
            this.rows = rows;
            this.columns = columns;
            this.numHorzBoundVar = numHorzBoundVar;
            this.numVertBoundVar = numVertBoundVar;
        }

        public Triple<double[][][], double[], double[]> backward(double[] dy) {
            double[][][] dx = new double[rows][columns][];
            double[] dh = new double[numHorzBoundVar];
            double[] dv = new double[numVertBoundVar];

            int cnt = root.backward(dy, dx, dh, dv, 0);

            if (cnt > 0) {
                for (int i = 0; i < numHorzBoundVar; i++) {
                    dh[i] /= cnt;
                }
                for (int i = 0; i < numVertBoundVar; i++) {
                    dv[i] /= cnt;
                }
            }

            return Triple.of(dx, dh, dv);
        }

    }

    class Encode extends Node {
        public final int row, col;

        public Encode(double[] values, int row, int col) {
            super(values);
            this.row = row;
            this.col = col;
        }

        @Override
        public int backward(double[] dy, double[][][] dx, double[] sdh, double[] sdv, int cnt) {
            dx[row][col] = dy;
            return cnt;
        }

    }

    abstract class Node {
        public final double[] values;

        public Node(double[] values) {
            this.values = values;
        }

        public abstract int backward(double[] dy, double[][][] dx, double[] sdh, double[] sdv, int cnt);

    }

    class HorzFold extends Fold {
        final H horzMem;

        public HorzFold(Node fst, Node snd, H horzMem, double[] values) {
            super(fst, snd, values);
            this.horzMem = horzMem;
        }

        @Override
        double[] backward(double[] dy, double[] sdh, double[] sdv) {
            Pair<double[], double[]> dxh = horzFold.backward(horzMem, dy);
            addWithNormalization(sdh, dxh.getRight());
            return dxh.getLeft();
        }
    }

    class VertFold extends Fold {
        final V vertMem;

        public VertFold(Node fst, Node snd, V vertMem, double[] values) {
            super(fst, snd, values);
            this.vertMem = vertMem;
        }

        @Override
        double[] backward(double[] dy, double[] sdh, double[] sdv) {
            Pair<double[], double[]> dxv = vertFold.backward(vertMem, dy);
            addWithNormalization(sdv, dxv.getRight());
            return dxv.getLeft();
        }
    }

    abstract class Fold extends Node {
        final Node fst, snd;

        public Fold(Node fst, Node snd, double[] values) {
            super(values);
            this.fst = fst;
            this.snd = snd;
        }

        void addWithNormalization(double[] src, double[] dst) {
            double sum = 1e-6;
            for (int i = 0; i < src.length; i++) {
                sum += src[i] * src[i];
            }
            double scale = 1 / Math.sqrt(sum);
            for (int i = 0; i < src.length; i++) {
                dst[i] += src[i] * scale;
            }
        }

        abstract double[] backward(double[] dy, double[] sdh, double[] sdv);

        @Override
        public int backward(double[] dy, double[][][] dInput, double[] sdh, double[] sdv, int cnt) {
            double[] dx = backward(dy, sdh, sdv);

            double[] da = new double[depth];
            double[] db = new double[depth];

            System.arraycopy(dx, 0, da, 0, depth);
            System.arraycopy(dx, depth, db, 0, depth);

            cnt = fst.backward(da, dInput, sdh, sdv, cnt + 1);
            cnt = snd.backward(db, dInput, sdh, sdv, cnt + 0);
            return cnt;
        }

    }

    // TODO set variables
    final DiffStruct<Pair<double[], double[]>, H, double[]> horzFold = null;
    final DiffStruct<Pair<double[], double[]>, V, double[]> vertFold = null;

    final int depth = 0;

    public Fold buildNode(boolean horizontal, double[] foldBoundVar, Node left, Node right) {
        double[] x = new double[depth * 2];
        System.arraycopy(left.values, 0, x, 0, depth);
        System.arraycopy(right.values, 0, x, depth, depth);

        if (horizontal) {
            Pair<H, double[]> result = horzFold.forward(Pair.of(x, foldBoundVar));
            return new HorzFold(left, right, result.getLeft(), result.getRight());
        } else {
            Pair<V, double[]> result = vertFold.forward(Pair.of(x, foldBoundVar));
            return new VertFold(left, right, result.getLeft(), result.getRight());
        }
    }

    @Override
    public Pair<Memory, double[]> forward(Triple<double[][][], double[], double[]> input) {
        double[][][] dataset = input.getLeft();

        double[] horzBoundVar = input.getMiddle();
        double[] vertBoundVar = input.getRight();

        final int rows = dataset.length, columns = dataset[0].length; // TODO check length > 0!

        @SuppressWarnings("unchecked")
        Node[][] nodes = (Node[][]) new Object[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                nodes[row][col] = new Encode(dataset[row][col], row, col);
            }
        }

        int curRows = rows, curColumns = columns;

        while (curRows > 1 || curColumns > 1) {
            // TODO implement forward
        }

        Node root = nodes[0][0];
        return Pair.of(new Memory(root, rows, columns, horzBoundVar.length, vertBoundVar.length), root.values);
    }

    @Override
    public Triple<double[][][], double[], double[]> backward(Memory memory, double[] deltaOutput) {
        return memory.backward(deltaOutput);
    }

}
