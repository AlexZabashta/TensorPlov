package core;

import java.util.Arrays;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
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
    public final int firstBvLen, secndBvLen;

    public final MultiVarDiffStruct<M, R> secnd;

    public Pipe(MultiVarDiffStruct<L, M> first, MultiVarDiffStruct<M, R> secnd) {
        this.first = first;
        this.secnd = secnd;

        if (!first.outputType().equals(secnd.freeVarType())) {
            throw new IllegalArgumentException("first.outputType() != secnd.freeVarType()");
        }

        this.firstBvLen = first.boundVarShape().length();
        this.secndBvLen = secnd.boundVarShape().length();
    }

    @Override
    public BoundVarShape boundVarShape() {
        return first.boundVarShape().concat(secnd.boundVarShape());
    }

    @Override
    public Object freeVarType() {
        return first.freeVarType();
    }

    @Override
    public double[][] genBoundVars() {
        return ArrayUtils.addAll(first.genBoundVars(), secnd.genBoundVars());
    }

    @Override
    public Object outputType() {
        return secnd.outputType();
    }

    @Override
    public Result<Pair<L, double[][]>, R> result(L lft, double[]... boundVar) {

        double[][] fbv = Arrays.copyOfRange(boundVar, 0, firstBvLen);
        double[][] sbv = Arrays.copyOfRange(boundVar, firstBvLen, boundVar.length);

        Result<Pair<L, double[][]>, M> resultLM = first.result(lft, fbv);

        Function<M, Pair<L, double[][]>> df = resultLM.derivative();

        M mid = resultLM.value();
        Result<Pair<M, double[][]>, R> resultMR = secnd.result(mid, sbv);

        Function<R, Pair<M, double[][]>> ds = resultMR.derivative();

        return new Result<>(new Function<R, Pair<L, double[][]>>() {
            @Override
            public Pair<L, double[][]> apply(R dr) {
                Pair<M, double[][]> dMR = ds.apply(dr);
                Pair<L, double[][]> dLM = df.apply(dMR.getLeft());

                return Pair.of(dLM.getLeft(), ArrayUtils.addAll(dLM.getRight(), dMR.getRight()));
            }
        }, resultMR.value());
    }

    @Override
    public String toString() {
        return first + " => " + secnd;
    }

}
