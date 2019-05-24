package core;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public interface MultiVarDiffStruct<F, T> extends DiffFunct<Pair<F, double[][]>, T> {
    public static <F, T> MultiVarDiffStruct<F, T> convert(VarDiffStruct<F, T> varDiffStruct) {
        return new MultiVarDiffStruct<F, T>() {

            @Override
            public BoundVarShape boundVarShape() {
                return new BoundVarShape(varDiffStruct.numBoundVars());
            }

            @Override
            public Object freeVarType() {
                return varDiffStruct.freeVarType();
            }

            @Override
            public double[][] genBoundVars() {
                return new double[][] { varDiffStruct.genBoundVars() };
            }

            @Override
            public Object outputType() {
                return varDiffStruct.outputType();
            }

            @Override
            public Result<Pair<F, double[][]>, T> result(F freeVar, double[]... boundVar) {
                Result<Pair<F, double[]>, T> result = varDiffStruct.result(freeVar, boundVar[0]);
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
        };
    }

    public static <F, T> MultiVarDiffStruct<F, T> convertFun(DiffFunct<F, T> diffFunct) {
        return new MultiVarDiffStruct<F, T>() {

            @Override
            public BoundVarShape boundVarShape() {
                return new BoundVarShape();
            }

            @Override
            public Object freeVarType() {
                return diffFunct.inputType();
            }

            @Override
            public double[][] genBoundVars() {
                return new double[0][];
            }

            @Override
            public Object outputType() {
                return diffFunct.outputType();
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
        };
    }

    public BoundVarShape boundVarShape();

    public Object freeVarType();

    public double[][] genBoundVars();

    @Override
    public default Pair<Object, BoundVarShape> inputType() {
        return Pair.of(freeVarType(), boundVarShape());
    }

    public Result<Pair<F, double[][]>, T> result(F freeVar, double[]... boundVar);

    @Override
    public default Result<Pair<F, double[][]>, T> result(Pair<F, double[][]> input) {
        return result(input.getLeft(), input.getRight());
    }

}
