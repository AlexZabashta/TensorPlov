package core;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public class ParallelVDiffStruct implements VarDiffStruct<double[][][], double[][][]> {

    public final VarDiffStruct<double[], double[]> base;
    final int rows, cols, inputDepth, outputDepth;

    public ParallelVDiffStruct(VarDiffStruct<double[], double[]> base, int rows, int cols) {
        this.base = base;
        this.rows = rows;
        this.cols = cols;
        this.inputDepth = ((VectorShape) base.freeVarType()).length;
        this.outputDepth = ((VectorShape) base.outputType()).length;
    }

    @Override
    public TensorShape freeVarType() {
        return new TensorShape(rows, cols, inputDepth);
    }

    @Override
    public double[] genBoundVars() {
        return base.genBoundVars();
    }

    @Override
    public int numBoundVars() {
        return base.numBoundVars();
    }

    @Override
    public TensorShape outputType() {
        return new TensorShape(rows, cols, outputDepth);
    }

    @Override
    public Result<Pair<double[][][], double[]>, double[][][]> result(double[][][] input, double[] bounVar) {

        double[][][] output = new double[rows][cols][];

        @SuppressWarnings("unchecked")
        Function<double[], Pair<double[], double[]>>[][] der = new Function[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Result<Pair<double[], double[]>, double[]> result = base.result(input[row][col], bounVar);
                output[row][col] = result.value();
                der[row][col] = result.derivative();
            }
        }

        return new Result<>(new Function<double[][][], Pair<double[][][], double[]>>() {

            @Override
            public Pair<double[][][], double[]> apply(double[][][] deltaOutput) {
                double[][][] deltaInput = new double[rows][cols][];

                double[] deltaBounVar = new double[base.numBoundVars()];

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        double[] subOutput = deltaOutput[row][col];
                        Pair<double[], double[]> delta = der[row][col].apply(subOutput);
                        double[] subInput = delta.getLeft();
                        deltaInput[row][col] = subInput;

                        double[] dbv = delta.getRight();
                        for (int i = 0; i < deltaBounVar.length; i++) {
                            deltaBounVar[i] += dbv[i];
                        }
                    }
                }
                for (int i = 0; i < deltaBounVar.length; i++) {
                    deltaBounVar[i] /= rows * cols;
                }
                return Pair.of(deltaInput, deltaBounVar);
            }
        }, output);
    }

}
