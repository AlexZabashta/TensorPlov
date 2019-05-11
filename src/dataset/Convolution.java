package dataset;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffStruct;

public class Convolution<SubMemory> implements DiffStruct<Pair<double[][][], double[]>, Convolution<SubMemory>.Memory, double[]> {

    class Memory {
        final MemoryNode root;
        final int rows, columns, weights;

        public Memory(Convolution<SubMemory>.MemoryNode root, int rows, int columns, int weights) {
            this.root = root;
            this.rows = rows;
            this.columns = columns;
            this.weights = weights;
        }

        public Pair<double[][][], double[]> backward(double[] dy) {
            double[][][] dx = new double[rows][columns][];
            double[] dw = new double[weights];

            int cnt = root.backward(dy, dx, dw, 0);

            if (cnt > 0) {
                for (int i = 0; i < weights; i++) {
                    dw[i] /= cnt;
                }
            }
            return Pair.of(dx, dw);
        }

    }

    class MemoryDataset extends MemoryNode {
        public final int row, col;

        public MemoryDataset(double[] values, int row, int col) {
            super(values);
            this.row = row;
            this.col = col;
        }

        @Override
        public int backward(double[] dy, double[][][] dx, double[] dw, int cnt) {
            dx[row][col] = dy;
            return cnt;
        }

    }

    abstract class MemoryNode {
        public final double[] values;

        public MemoryNode(double[] values) {
            this.values = values;
        }

        public abstract int backward(double[] dy, double[][][] dx, double[] dw, int cnt);

    }

    class MemorySubNode extends MemoryNode {
        final MemoryNode left, right;
        final SubMemory memory;

        public MemorySubNode(MemoryNode left, MemoryNode right, SubMemory memory, double[] values) {
            super(values);
            this.left = left;
            this.right = right;
            this.memory = memory;
        }

        @Override
        public int backward(double[] dy, double[][][] dInput, double[] sumDW, int cnt) {

            Pair<double[], double[]> dxw = baseNet.backward(memory, dy);
            double[] dx = dxw.getLeft();
            double[] dw = dxw.getRight();
            for (int i = 0; i < dw.length; i++) {
                sumDW[i] += dw[i]; // TODO normalization?
            }

            double[] da = new double[depth];
            double[] db = new double[depth];

            System.arraycopy(dx, 0, da, 0, depth);
            System.arraycopy(dx, depth, db, 0, depth);

            cnt = left.backward(da, dInput, sumDW, cnt + 1);
            cnt = right.backward(db, dInput, sumDW, cnt);

            return cnt;
        }

    }

    final DiffStruct<Pair<double[], double[]>, SubMemory, double[]> baseNet = null;

    int depth;

    @Override
    public Pair<double[][][], double[]> backward(Convolution<SubMemory>.Memory memory, double[] dy) {
        return memory.backward(dy);
    }

    public MemorySubNode buildNode(double[] w, MemoryNode left, MemoryNode right) {
        double[] x = new double[depth * 2];
        System.arraycopy(left.values, 0, x, 0, depth);
        System.arraycopy(right.values, 0, x, depth, depth);
        Pair<SubMemory, double[]> result = baseNet.forward(Pair.of(x, w));
        return new MemorySubNode(left, right, result.getLeft(), result.getRight());

    }

    @Override
    public Pair<Convolution<SubMemory>.Memory, double[]> forward(Pair<double[][][], double[]> input) {
        double[][][] dataset = input.getLeft();
        double[] weight = input.getRight();

        final int rows = dataset.length, columns = dataset[0].length, weights = weight.length; // TODO check length > 0!

        @SuppressWarnings("unchecked")
        MemoryNode[][] nodes = (MemoryNode[][]) new Object[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                nodes[row][col] = new MemoryDataset(dataset[row][col], row, col);
            }
        }

        int curRows = rows, curColumns = columns;

        while (curRows > 1 || curColumns > 1) {
            // TODO implement forward
        }

        MemoryNode root = nodes[0][0];
        return Pair.of(new Memory(root, rows, columns, weights), root.values);
    }

}
