package core;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public class Concat<F> implements MultiVarDiffStruct<Pair<F, double[]>, double[]> {

    public final MultiVarDiffStruct<F, double[]> base;

    public Concat(MultiVarDiffStruct<F, double[]> base) {
        this.base = base;
    }

    @Override
    public int[] numBoundVars() {
        return base.numBoundVars();
    }

    @Override
    public Result<Pair<Pair<F, double[]>, double[][]>, double[]> result(Pair<F, double[]> freeVar, double[]... bounVar) {
        Result<Pair<F, double[][]>, double[]> result = base.result(freeVar.getLeft(), bounVar);

        Function<double[], Pair<F, double[][]>> der = result.derivative();

        double[] first = result.value();
        double[] secnd = freeVar.getRight();

        int firstLength = first.length;
        int secndLength = secnd.length;

        double[] vector = new double[firstLength + secndLength];
        System.arraycopy(first, 0, vector, 0, firstLength);
        System.arraycopy(secnd, 0, vector, firstLength, secndLength);

        return new Result<>(new Function<double[], Pair<Pair<F, double[]>, double[][]>>() {
            @Override
            public Pair<Pair<F, double[]>, double[][]> apply(double[] dy) {
                double[] df = new double[firstLength];
                double[] ds = new double[secndLength];
                System.arraycopy(dy, 0, df, 0, firstLength);
                System.arraycopy(dy, firstLength, ds, 0, secndLength);

                Pair<F, double[][]> dx = der.apply(df);
                return Pair.of(Pair.of(dx.getLeft(), ds), dx.getRight());
            }
        }, vector);
    }

    @Override
    public double[][] genBoundVars() {
        return base.genBoundVars();
    }

}
