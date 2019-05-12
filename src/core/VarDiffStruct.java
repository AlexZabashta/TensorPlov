package core;

import org.apache.commons.lang3.tuple.Pair;

public interface VarDiffStruct<F, M, T> extends DiffStruct<Pair<F, double[]>, M, T> {
    public int numBoundVars();

    @SuppressWarnings("unchecked")
    @Override
    default Class<Pair<F, double[]>> inputClass() {
        return (Class<Pair<F, double[]>>) Pair.of(null, null).getClass();
    }

    @Override
    public default Pair<M, T> forward(Pair<F, double[]> input) {
        return forward(input.getLeft(), input.getRight());
    }

    public Pair<M, T> forward(F freeVar, double[] bounVar);

//    @Override
//    public default Pair<F, double[]> backward(M memory, T deltaOutput) {
//        double[] deltaBoundVar = new double[numBoundVar()];
//        return Pair.of(backward(memory, deltaOutput, deltaBoundVar), deltaBoundVar);
//    }
//
//    public default F backward(M memory, T deltaOutput, double[] deltaBoundVar) {
//        Pair<F, double[]> pair = backward(memory, deltaOutput);
//
//        double[] dBoundVar = pair.getRight();
//        for (int i = 0; i < dBoundVar.length; i++) {
//            deltaBoundVar[i] += dBoundVar[i];
//        }
//        return pair.getLeft();
//    }

}
