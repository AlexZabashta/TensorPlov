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

public abstract class Convolution implements MultiVarDiffStruct<double[][][], double[][][]> {
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

            addWithWeight(dxh.getRight(), 1.0, sdh);
            return dxh.getLeft();
        }
    }

    public class Memory implements Function<double[][][], Pair<double[][][], double[][]>> {
        final Node[][] root;

        Memory(Node[][] root) {
            this.root = root;
        }

        @Override
        public Pair<double[][][], double[][]> apply(double[][][] dy) {
            double[][][] dx = new double[rowsInp][colsInp][];
            double[] dh = new double[horzFold.numBoundVars()];
            double[] dv = new double[vertFold.numBoundVars()];

            for (int row = 0; row < rowsOut; row++) {
                for (int col = 0; col < colsOut; col++) {
                    root[row][col].backward(this, dy[row][col], dx, dh, dv, 1, 1);
                }
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

    }

    class VertFold extends Fold {
        VertFold(Node first, Node secnd, Function<double[], Pair<double[], double[]>> derivative, double[] values) {
            super(first, secnd, derivative, values);
        }

        @Override
        double[] backward(Memory memory, double[] dy, double[] sdh, double[] sdv) {
            Pair<double[], double[]> dxv = derivative.apply(dy);
            addWithWeight(dxv.getRight(), 1.0, sdv);

            return dxv.getLeft();
        }
    }

    public final int depth;

    public final VarDiffStruct<double[], double[]> horzFold;
    final int rowsInp, colsInp;
    final int rowsOut, colsOut;
    public final VarDiffStruct<double[], double[]> vertFold;

    public Convolution(int rowsInp, int colsInp, int rowsOut, int colsOut, VarDiffStruct<double[], double[]> horzFold, VarDiffStruct<double[], double[]> vertFold) {
        this.rowsInp = rowsInp;
        this.colsInp = colsInp;
        this.rowsOut = rowsOut;
        this.colsOut = colsOut;
        this.depth = ((VectorShape) horzFold.outputType()).length;
        this.horzFold = horzFold;
        this.vertFold = vertFold;

        if (rowsInp < rowsOut) {
            throw new IllegalArgumentException("rowsInp < rowsOut");
        }

        if (colsInp < colsOut) {
            throw new IllegalArgumentException("colsInp < colsOut");
        }

        if (depth != ((VectorShape) vertFold.outputType()).length) {
            throw new IllegalArgumentException("vertFold.outputType().length != depth");
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
        return new TensorShape(rowsInp, colsInp, depth);
    }

    @Override
    public double[][] genBoundVars() {
        return new double[][] { horzFold.genBoundVars(), vertFold.genBoundVars() };
    }

    @Override
    public TensorShape outputType() {
        return new TensorShape(rowsOut, colsOut, depth);
    }

    @Override
    public Result<Pair<double[][][], double[][]>, double[][][]> result(double[][][] freeVar, double[]... bounVar) {
        return result(freeVar, bounVar[0], bounVar[1]);
    }

    public Result<Pair<double[][][], double[][]>, double[][][]> result(double[][][] dataset, double[] horzBoundVar, double[] vertBoundVar) {

        Node[][] nodes = new Node[rowsInp][colsInp];

        for (int row = 0; row < rowsInp; row++) {
            for (int col = 0; col < colsInp; col++) {
                nodes[row][col] = new DatasetCell(dataset[row][col], row, col);
            }
        }
        return result(nodes, horzBoundVar, vertBoundVar);
    }

    abstract Result<Pair<double[][][], double[][]>, double[][][]> result(Node[][] nodes, double[] horzBoundVar, double[] vertBoundVar);

}
