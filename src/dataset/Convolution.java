package dataset;

import java.util.Arrays;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import core.BoundVarShape;
import core.MultiVarDiffStruct;
import core.Result;
import core.TensorShape;
import core.VarDiffStruct;
import core.VectorShape;

public abstract class Convolution implements MultiVarDiffStruct<double[][][], double[]> {
    class DatasetCell extends Node {
        final int row, col;

        DatasetCell(double[] values, int row, int col) {
            super(1, values);
            this.row = row;
            this.col = col;
        }

        @Override
        void backward(Memory memory, double[] dy, double[][][] dx, double[] sdh, double[] sdv, double sumErrors, double normErrors) {
            dx[row][col] = dy;
        }

    }

    abstract class Fold extends Node {
        final Function<double[], Pair<double[], double[]>> derivative;
        final Node first, secnd;

        Fold(Node first, Node secnd, Function<double[], Pair<double[], double[]>> derivative, double[] values) {
            super(first.size + secnd.size, values);
            this.first = first;
            this.secnd = secnd;
            this.derivative = derivative;
        }

        void addWithWeight(double[] src, double weight, double[] dst) {
            for (int i = 0; i < src.length; i++) {
                dst[i] += src[i] * weight;
            }
        }

        abstract double[] backward(Memory memory, double[] dy, double[] sdh, double[] sdv);

        @Override
        void backward(Memory memory, double[] dy, double[][][] dInput, double[] sdh, double[] sdv, double sumErrors, double normErrors) {
            double[] dx = backward(memory, dy, sdh, sdv);

            double[] da = Arrays.copyOfRange(dx, 0, depth);
            double[] db = Arrays.copyOfRange(dx, depth, dx.length);

            first.backward(memory, da, dInput, sdh, sdv, sumErrors, normErrors);
            secnd.backward(memory, db, dInput, sdh, sdv, sumErrors, normErrors);
        }

    }

    class HorzFold extends Fold {
        HorzFold(Node first, Node secnd, Function<double[], Pair<double[], double[]>> derivative, double[] values) {
            super(first, secnd, derivative, values);
        }

        @Override
        double[] backward(Memory memory, double[] dy, double[] sdh, double[] sdv) {
            Pair<double[], double[]> dxh = derivative.apply(dy);
            addWithWeight(dxh.getRight(), weight(), sdh);
            memory.normH += weight();
            return dxh.getLeft();
        }
    }

    public class Memory implements Function<double[], Pair<double[][][], double[][]>> {
        double normH, normV;
        final Node root;

        Memory(Node root) {
            this.root = root;
        }

        @Override
        public Pair<double[][][], double[][]> apply(double[] dy) {
            double[][][] dx = new double[rows][cols][];
            double[] dh = new double[horzFold.numBoundVars()];
            double[] dv = new double[vertFold.numBoundVars()];

            normH = 0;
            normV = 0;
            root.backward(this, dy, dx, dh, dv, 1, 1);

            if (normH > 0) {
                normalize(dh, 1.0 / normH);
            }

            if (normV > 0) {
                normalize(dv, 1.0 / normV);
            }

            return Pair.of(dx, new double[][] { dh, dv });
        }

        void normalize(double[] array, double scale) {
            for (int i = 0; i < array.length; i++) {
                array[i] *= scale;
            }
        }

    }

    abstract class Node {
        final int size;
        final double[] values;

        Node(int size, double[] values) {
            this.size = size;
            this.values = values;
        }

        abstract void backward(Memory memory, double[] dy, double[][][] dx, double[] sdh, double[] sdv, double sumErrors, double normErrors);

        double weight() {
            return size;
        }

    }

    class VertFold extends Fold {
        VertFold(Node first, Node secnd, Function<double[], Pair<double[], double[]>> derivative, double[] values) {
            super(first, secnd, derivative, values);
        }

        @Override
        double[] backward(Memory memory, double[] dy, double[] sdh, double[] sdv) {
            Pair<double[], double[]> dxv = derivative.apply(dy);
            addWithWeight(dxv.getRight(), weight(), sdv);
            memory.normV += weight();
            return dxv.getLeft();
        }
    }

    public final int depth;

    public final VarDiffStruct<double[], double[]> horzFold;
    final int rows, cols;
    public final VarDiffStruct<double[], double[]> vertFold;

    public Convolution(int rows, int cols, VarDiffStruct<double[], double[]> horzFold, VarDiffStruct<double[], double[]> vertFold) {
        this.rows = rows;
        this.cols = cols;
        this.horzFold = horzFold;
        this.vertFold = vertFold;

        this.depth = ((VectorShape) horzFold.outputType()).length;
        if (depth != ((VectorShape) vertFold.outputType()).length) {
            throw new IllegalArgumentException("horzFold.outputType().length != vertFold.outputType().length");
        }

        if (2 * depth != ((VectorShape) horzFold.freeVarType()).length) {
            throw new IllegalArgumentException("horzFold.freeVarType().length != 2 * depth");
        }

        if (2 * depth != ((VectorShape) vertFold.freeVarType()).length) {
            throw new IllegalArgumentException("vertFold.freeVarType().length != 2 * depth");
        }
    }

    @Override
    public BoundVarShape boundVarShape() {
        return new BoundVarShape(horzFold.numBoundVars(), vertFold.numBoundVars());
    }

    Fold buildNode(boolean horizontal, double[] foldBoundVar, Node first, Node secnd) {
        double[] x = ArrayUtils.addAll(first.values, secnd.values);
        if (horizontal) {
            Result<Pair<double[], double[]>, double[]> result = horzFold.result(x, foldBoundVar);
            return new HorzFold(first, secnd, result.derivative(), result.value());
        } else {
            Result<Pair<double[], double[]>, double[]> result = vertFold.result(x, foldBoundVar);
            return new VertFold(first, secnd, result.derivative(), result.value());
        }
    }

    @Override
    public TensorShape freeVarType() {
        return new TensorShape(rows, cols, depth);
    }

    @Override
    public double[][] genBoundVars() {
        return new double[][] { horzFold.genBoundVars(), vertFold.genBoundVars() };
    }

    @Override
    public VectorShape outputType() {
        return new VectorShape(depth);
    }

    @Override
    public Result<Pair<double[][][], double[][]>, double[]> result(double[][][] freeVar, double[]... bounVar) {
        return result(freeVar, bounVar[0], bounVar[1]);
    }

    public Result<Pair<double[][][], double[][]>, double[]> result(double[][][] dataset, double[] horzBoundVar, double[] vertBoundVar) {

        Node[][] nodes = new Node[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                nodes[row][col] = new DatasetCell(dataset[row][col], row, col);
            }
        }
        return result(nodes, horzBoundVar, vertBoundVar);
    }

    abstract Result<Pair<double[][][], double[][]>, double[]> result(Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar);

}
