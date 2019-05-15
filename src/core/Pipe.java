package core;

import java.util.Arrays;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public class Pipe<L, M, R> implements MultiVarDiffStruct<L, R> {

    public static <A, B, C> Pipe<A, B, C> of(MultiVarDiffStruct<A, B> ab, MultiVarDiffStruct<B, C> bc) {
        return new Pipe<>(ab, bc);
    }

    public static <A, B, C, D> Pipe<A, C, D> of(MultiVarDiffStruct<A, B> ab, MultiVarDiffStruct<B, C> bc, MultiVarDiffStruct<C, D> cd) {
        return new Pipe<>(new Pipe<>(ab, bc), cd);
    }

    public static <A, B, C, D, E> Pipe<A, C, E> of(MultiVarDiffStruct<A, B> ab, MultiVarDiffStruct<B, C> bc, MultiVarDiffStruct<C, D> cd, MultiVarDiffStruct<D, E> de) {
        return new Pipe<>(new Pipe<>(ab, bc), new Pipe<>(cd, de));
    }

    public final MultiVarDiffStruct<L, M> first;
    public final MultiVarDiffStruct<M, R> secnd;
    public final int firstLength, secndLength;
    private final int[] numBoundVars;

    public Pipe(MultiVarDiffStruct<L, M> first, MultiVarDiffStruct<M, R> secnd) {
        this.first = first;
        this.secnd = secnd;
        int[] firstNum = first.numBoundVars();
        int[] secndNum = secnd.numBoundVars();
        firstLength = firstNum.length;
        secndLength = secndNum.length;

        numBoundVars = Arrays.copyOf(firstNum, firstLength + secndLength);
        System.arraycopy(secndNum, 0, numBoundVars, firstLength, secndLength);
    }

    @Override
    public int[] numBoundVars() {
        return numBoundVars.clone();
    }

    @Override
    public Result<Pair<L, double[][]>, R> result(L lft, double[]... bounVar) {
        double[][] firstBounVar = Arrays.copyOf(bounVar, firstLength);
        double[][] secndBounVar = Arrays.copyOfRange(bounVar, firstLength, numBoundVars.length);

        Result<Pair<L, double[][]>, M> resultLM = first.result(lft, firstBounVar);

        Function<M, Pair<L, double[][]>> df = resultLM.derivative();

        M mid = resultLM.value();
        Result<Pair<M, double[][]>, R> resultMR = secnd.result(mid, secndBounVar);

        Function<R, Pair<M, double[][]>> ds = resultMR.derivative();

        return new Result<>(new Function<R, Pair<L, double[][]>>() {
            @Override
            public Pair<L, double[][]> apply(R dr) {
                Pair<M, double[][]> dMR = ds.apply(dr);
                Pair<L, double[][]> dLM = df.apply(dMR.getLeft());

                double[][] dbv = Arrays.copyOf(dLM.getRight(), firstLength + secndLength);
                System.arraycopy(dMR.getRight(), 0, dbv, firstLength, secndLength);

                return Pair.of(dLM.getLeft(), dbv);
            }
        }, resultMR.value());
    }

    @Override
    public String toString() {
        return first + " => " + secnd;
    }

}
