package core;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public interface MultiVarDiffStruct<F, T> extends DiffFunct<Pair<F, double[][]>, T> {
    public static <F, T> MultiVarDiffStruct<F, T> convert(VarDiffStruct<F, T> varDiffStruct) {
        return new MultiVarDiffStruct<F, T>() {

            @Override
            public int[] numBoundVars() {
                return new int[] { varDiffStruct.numBoundVars() };
            }

            @Override
            public Result<Pair<F, double[][]>, T> result(F freeVar, double[]... bounVar) {
                Result<Pair<F, double[]>, T> result = varDiffStruct.result(freeVar, bounVar[0]);
                return new Result<Pair<F, double[][]>, T>(new Function<T, Pair<F, double[][]>>() {
                    @Override
                    public Pair<F, double[][]> apply(T delta) {
                        Pair<F, double[]> pair = result.apply(delta);
                        return Pair.of(pair.getLeft(), new double[][] { pair.getRight() });
                    }
                }, result.value());
            }

            @Override
            public String toString() {
                return varDiffStruct.toString();
            }

            @Override
            public double[][] genBoundVars() {
                return new double[][] { varDiffStruct.genBoundVars() };
            }
        };
    }

    public static <F, T> MultiVarDiffStruct<F, T> convertDS(DiffFunct<F, T> diffFunct) {
        return new MultiVarDiffStruct<F, T>() {

            @Override
            public int[] numBoundVars() {
                return new int[0];
            }

            @Override
            public Result<Pair<F, double[][]>, T> result(F input, double[]... empty) {

                Result<F, T> result = diffFunct.result(input);

                return new Result<Pair<F, double[][]>, T>(new Function<T, Pair<F, double[][]>>() {
                    @Override
                    public Pair<F, double[][]> apply(T deltaOutput) {
                        F deltaInput = result.apply(deltaOutput);
                        return Pair.of(deltaInput, new double[0][]);
                    }
                }, result.value());
            }

            @Override
            public String toString() {
                return diffFunct.toString();
            }

            @Override
            public double[][] genBoundVars() {
                return new double[0][];
            }
        };
    }

    public int[] numBoundVars();

    public double[][] genBoundVars();

    public Result<Pair<F, double[][]>, T> result(F freeVar, double[]... bounVar);

    @Override
    public default Result<Pair<F, double[][]>, T> result(Pair<F, double[][]> input) {
        return result(input.getLeft(), input.getRight());
    }

}
