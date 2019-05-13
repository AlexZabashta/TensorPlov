package dataset;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffFunct;
import core.Result;

public class Deconvolution implements DiffFunct<double[], double[][][]> {

    class Memory implements Function<double[][][], double[]> {

        @Override
        public double[] apply(double[][][] dy) {
            return null;
        }

    }

    @Override
    public Result<double[], double[][][]> result(double[] input) {
        // TODO Auto-generated method stub
        return null;
    }

}
