import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public class DoubleFun implements DiffFunction<double[], double[][]> {

    @Override
    public Pair<Function<double[][], double[]>, double[][]> forward(double[] input) {
        return Pair.of(new Memory(), new double[][] { input });
    }

    class Memory implements Function<double[][], double[]> {

        @Override
        public double[] apply(double[][] t) {
            return t[0];
        }
    }

}
