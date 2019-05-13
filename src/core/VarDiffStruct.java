package core;

import org.apache.commons.lang3.tuple.Pair;

public interface VarDiffStruct<F, T> extends DiffFunct<Pair<F, double[]>, T> {
    public int numBoundVars();

    @Override
    public default Result<Pair<F, double[]>, T> result(Pair<F, double[]> input) {
        return result(input.getLeft(), input.getRight());
    }

    public Result<Pair<F, double[]>, T> result(F freeVar, double[] bounVar);

    // @Override
    // public default Pair<F, double[]> backward(M memory, T deltaOutput) {
    // double[] deltaBoundVar = new double[numBoundVar()];
    // return Pair.of(backward(memory, deltaOutput, deltaBoundVar), deltaBoundVar);
    // }
    //
    // public default F backward(M memory, T deltaOutput, double[] deltaBoundVar) {
    // Pair<F, double[]> pair = backward(memory, deltaOutput);
    //
    // double[] dBoundVar = pair.getRight();
    // for (int i = 0; i < dBoundVar.length; i++) {
    // deltaBoundVar[i] += dBoundVar[i];
    // }
    // return pair.getLeft();
    // }

}
