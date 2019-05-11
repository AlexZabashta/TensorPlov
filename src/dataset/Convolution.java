package dataset;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffStruct;

public class Convolution<H, V> implements DiffStruct<Triple<double[][][], double[], double[]>, Convolution<H, V>.Memory, double[]> {

    public class Memory {
        final Node root;
        final int rows, cols;
        final int cntH, cntV;

        Memory(Node root, int rows, int cols, int cntH, int cntV) {
            this.root = root;
            this.rows = rows;
            this.cols = cols;
            this.cntH = cntH;
            this.cntV = cntV;
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

            if (cntH > 0) {
                normalize(dh, 1.0 / cntH);
            }

            if (cntV > 0) {
                normalize(dv, 1.0 / cntV);
            }

            return Triple.of(dx, dh, dv);
        }

    }

    abstract class Node {
        final double[] values;

        Node(double[] values) {
            this.values = values;
        }

        abstract void backward(double[] dy, double[][][] dx, double[] sdh, double[] sdv);

    }

    abstract class Fold extends Node {
        final Node first, secnd;

        Fold(Node first, Node secnd, double[] values) {
            super(values);
            this.first = first;
            this.secnd = secnd;
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
            addWithNormalization(sdh, dxh.getRight());
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
            addWithNormalization(sdv, dxv.getRight());
            return dxv.getLeft();
        }
    }

    class DatasetCell extends Node {
        final int row, col;

        DatasetCell(double[] values, int row, int col) {
            super(values);
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
        Node[][] nodes = (Node[][]) new Object[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                nodes[row][col] = new DatasetCell(dataset[row][col], row, col);
            }
        }

        int curRows = rows, curCols = cols;
        int cntH = 0, cntV = 0;

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
                            sumD += nodes[rowD][col].values[i];
                            sumU += nodes[rowU][col].values[i];
                            double diff = nodes[rowD][col].values[i] - nodes[rowU][col].values[i];
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
                            sumL += nodes[row][colL].values[i];
                            sumR += nodes[row][colR].values[i];
                            double diff = nodes[row][colL].values[i] - nodes[row][colR].values[i];
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
                    nodes[row][colA] = buildNode(true, horzBoundVar, nodes[row][colA], nodes[row][colB]);
                    nodes[row][colB] = nodes[row][curCols];
                    ++cntH;
                }
            } else {
                --curRows;

                for (int col = 0; col < curCols; col++) {
                    nodes[rowA][col] = buildNode(false, vertBoundVar, nodes[rowA][col], nodes[rowB][col]);
                    nodes[rowB][col] = nodes[curRows][col];
                    ++cntV;
                }
            }
        }

        Node root = nodes[0][0];
        return Pair.of(new Memory(root, rows, cols, cntH, cntV), root.values);
    }

}
