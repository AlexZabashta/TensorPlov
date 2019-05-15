package core;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public class ParallelVDiffStruct implements VarDiffStruct<double[][][], double[][][]> {

    public final VarDiffStruct<double[], double[]> base;
    public final boolean transferError;

    public ParallelVDiffStruct(boolean transferError, VarDiffStruct<double[], double[]> base) {
        this.transferError = transferError;
        this.base = base;
    }

    @Override
    public int numBoundVars() {
        return base.numBoundVars();
    }

    @Override
    public Result<Pair<double[][][], double[]>, double[][][]> result(double[][][] input, double[] bounVar) {
        final int rows = input.length, cols = input[0].length;
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

                        if (transferError) {
                            double sum = 0;
                            for (int i = 0; i < subOutput.length; i++) {
                                sum += subOutput[i] * subOutput[i];
                            }
                            double error = Math.sqrt(sum);
                            for (int i = 0; i < subInput.length; i++) {
                                subInput[i] *= error;
                            }
                        }
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
