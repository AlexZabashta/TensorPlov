package dataset;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffStruct;

public class Convolution<H, V> implements DiffStruct<Triple<double[][][], double[], double[]>, Convolution<H, V>.Memory, double[]> {

    public class Memory {
        final Node root;
        final int rows, cols;
        final int normH, normV;

        Memory(Node root, int rows, int cols, int normH, int normV) {
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
            double[] dh = new double[numHorzBoundVar];
            double[] dv = new double[numVertBoundVar];

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

    }

    abstract class Fold extends Node {
        final Node first, secnd;

        Fold(Node first, Node secnd, double[] values) {
            super(Math.max(first.level, secnd.level) + 1, values);
            this.first = first;
            this.secnd = secnd;
        }

        void addWithNormalization(int weight, double[] src, double[] dst) {
            double sum = 1e-9;
            for (int i = 0; i < src.length; i++) {
                sum += src[i] * src[i];
            }
            double scale = weight / Math.sqrt(sum);
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
            addWithNormalization(level, sdh, dxh.getRight());
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
            addWithNormalization(level, sdv, dxv.getRight());
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

    public final int depth, numHorzBoundVar, numVertBoundVar;
    public final boolean symmetric;
    public final DiffStruct<Pair<double[], double[]>, H, double[]> horzFold;
    public final DiffStruct<Pair<double[], double[]>, V, double[]> vertFold;

    public Convolution(boolean symmetric, int depth, DiffStruct<Pair<double[], double[]>, H, double[]> horzFold, int numHorzBoundVar, DiffStruct<Pair<double[], double[]>, V, double[]> vertFold, int numVertBoundVar) {
        this.symmetric = symmetric;
        this.depth = depth;
        this.horzFold = horzFold;
        this.numHorzBoundVar = numHorzBoundVar;
        this.vertFold = vertFold;
        this.numVertBoundVar = numVertBoundVar;
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
            Pair<H, double[]> result = horzFold.forward(Pair.of(x, foldBoundVar));
            return new HorzFold(first, secnd, result.getLeft(), result.getRight());
        } else {
            Pair<V, double[]> result = vertFold.forward(Pair.of(x, foldBoundVar));
            return new VertFold(first, secnd, result.getLeft(), result.getRight());
        }
    }

    @Override
    public Pair<Memory, double[]> forward(Triple<double[][][], double[], double[]> input) {
        double[][][] dataset = input.getLeft();

        double[] horzBoundVar = input.getMiddle();
        double[] vertBoundVar = input.getRight();

        final int rows = dataset.length, cols = dataset[0].length;

        @SuppressWarnings("unchecked")
        Node[][] nodes = (Node[][]) Array.newInstance(Node.class, rows, cols);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                nodes[row][col] = new DatasetCell(dataset[row][col], row, col);
            }
        }

        int curRows = rows, curCols = cols;
        int normH = 0, normV = 0;

        while (curRows > 1 || curCols > 1) {
            double rowSim = Double.POSITIVE_INFINITY, colSim = Double.POSITIVE_INFINITY;
            int rowA = 0, rowB = 1;
            int colA = 0, colB = 1;

            for (int rowU = 1; rowU < curRows; rowU++) {
                for (int rowD = symmetric ? 0 : (rowU - 1); rowD < rowU; rowD++) {
                    double sim = 0;
                    double sumD = 0, sumU = 0;
                    for (int col = 0; col < curCols; col++) {
                        for (int i = 0; i < depth; i++) {
                            double valD = (nodes[rowD][col]).values[i];
                            double valU = (nodes[rowU][col]).values[i];

                            sumD += valD;
                            sumU += valU;
                            double diff = valD - valU;
                            sim += diff * diff;
                        }
                    }

                    sim /= curCols;

                    if (sim < rowSim) {
                        rowSim = sim;
                        rowA = rowD;
                        rowB = rowU;

                        if (symmetric && sumD > sumU) {
                            rowA ^= rowB;
                            rowB ^= rowA;
                            rowA ^= rowB;
                        }
                    }
                }
            }

            for (int colR = 1; colR < curCols; colR++) {
                for (int colL = symmetric ? 0 : (colR - 1); colL < colR; colL++) {
                    double sim = 0;
                    double sumL = 0, sumR = 0;
                    for (int row = 0; row < curRows; row++) {
                        for (int i = 0; i < depth; i++) {
                            double valL = (nodes[row][colL]).values[i];
                            double valR = (nodes[row][colR]).values[i];

                            sumL += valL;
                            sumR += valR;
                            double diff = valL - valR;
                            sim += diff * diff;

                        }
                    }

                    sim /= curRows;

                    if (sim < colSim) {
                        colSim = sim;
                        colA = colL;
                        colB = colR;

                        if (symmetric && sumL > sumR) {
                            colA ^= colB;
                            colB ^= colA;
                            colA ^= colB;
                        }
                    }
                }
            }

            if (colSim < rowSim) {
                --curCols;
                for (int row = 0; row < curRows; row++) {
                    Node node = buildNode(true, horzBoundVar, nodes[row][colA], nodes[row][colB]);
                    nodes[row][colA] = node;
                    nodes[row][colB] = nodes[row][curCols];
                    normH += node.level;
                }
            } else {
                --curRows;

                for (int col = 0; col < curCols; col++) {
                    Node node = buildNode(false, vertBoundVar, nodes[rowA][col], nodes[rowB][col]);
                    nodes[rowA][col] = node;
                    nodes[rowB][col] = nodes[curRows][col];
                    normV += node.level;
                }
            }
        }

        Node root = nodes[0][0];
        return Pair.of(new Memory(root, rows, cols, normH, normV), root.values);
    }

}
