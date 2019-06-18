package dataset;

import java.util.function.Function;

import core.DiffFunct;
import core.Result;
import core.TensorShape;
import core.VectorShape;

public class Pac implements DiffFunct<double[], double[][][]> {
    public final int depth;

    public Pac(int depth) {

        this.depth = depth;
    }

    @Override
    public VectorShape inputType() {
        return new VectorShape(depth);
    }

    @Override
    public TensorShape outputType() {
        return new TensorShape(1, 1, depth);
    }

    @Override
    public Result<double[], double[][][]> result(double[] input) {

        return new Result<double[], double[][][]>(new Function<double[][][], double[]>() {
            @Override
            public double[] apply(double[][][] output) {
                return output[0][0];
            }
        }, new double[][][] { { input } });
    }

}
