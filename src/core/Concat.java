package core;

import java.util.Arrays;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Concat<F, S> implements MultiVarDiffStruct<Pair<F, S>, double[]> {

    public final MultiVarDiffStruct<F, double[]> first;
    public final MultiVarDiffStruct<S, double[]> secnd;

    public final int firstOutputLength, secndOutputLength;
    public final int firstBvLen, secndBvLen;

    public Concat(MultiVarDiffStruct<F, double[]> first, MultiVarDiffStruct<S, double[]> secnd) {
        this.first = first;
        this.secnd = secnd;

        firstOutputLength = ((VectorShape) first.outputType()).length;
        secndOutputLength = ((VectorShape) secnd.outputType()).length;

        this.firstBvLen = first.boundVarShape().length();
        this.secndBvLen = secnd.boundVarShape().length();

    }

    @Override
    public BoundVarShape boundVarShape() {
        return first.boundVarShape().concat(secnd.boundVarShape());
    }

    @Override
    public Object freeVarType() {
        return Pair.of(first.freeVarType(), secnd.freeVarType());
    }

    @Override
    public double[][] genBoundVars() {
        return ArrayUtils.addAll(first.genBoundVars(), secnd.genBoundVars());
    }

    @Override
    public Object outputType() {
        return new VectorShape(firstOutputLength + secndOutputLength);
    }

    @Override
    public Result<Pair<Pair<F, S>, double[][]>, double[]> result(Pair<F, S> freeVar, double[]... boundVar) {

        double[][] fbv = Arrays.copyOfRange(boundVar, 0, firstBvLen);
        double[][] sbv = Arrays.copyOfRange(boundVar, firstBvLen, secndBvLen);

        Result<Pair<F, double[][]>, double[]> firstResult = first.result(freeVar.getLeft(), fbv);
        Result<Pair<S, double[][]>, double[]> secndResult = secnd.result(freeVar.getRight(), sbv);

        Function<double[], Pair<F, double[][]>> firstDer = firstResult.derivative();
        Function<double[], Pair<S, double[][]>> secndDer = secndResult.derivative();

        double[] vector = ArrayUtils.addAll(firstResult.value(), secndResult.value());

        return new Result<>(new Function<double[], Pair<Pair<F, S>, double[][]>>() {

            @Override
            public Pair<Pair<F, S>, double[][]> apply(double[] dy) {
                double[] df = Arrays.copyOfRange(dy, 0, firstOutputLength);
                double[] ds = Arrays.copyOfRange(dy, firstOutputLength, secndOutputLength);

                Pair<F, double[][]> fdx = firstDer.apply(df);
                Pair<S, double[][]> sdx = secndDer.apply(ds);

                return Pair.of(Pair.of(fdx.getLeft(), sdx.getLeft()), ArrayUtils.addAll(fdx.getRight(), sdx.getRight()));
            }
        }, vector);
    }

}
