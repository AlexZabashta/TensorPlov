package core;

import org.apache.commons.lang3.tuple.Pair;

public interface VarDiffStruct<F, T> extends DiffFunct<Pair<F, double[]>, T> {
    public Object freeVarType();

    public double[] genBoundVars();

    @Override
    public default Pair<Object, TensorShape> inputType() {
        return Pair.of(freeVarType(), new TensorShape(numBoundVars()));
    }

    public int numBoundVars();

    public Result<Pair<F, double[]>, T> result(F freeVar, double[] boundVar);

    @Override
    public default Result<Pair<F, double[]>, T> result(Pair<F, double[]> input) {
        return result(input.getLeft(), input.getRight());
    }

}
