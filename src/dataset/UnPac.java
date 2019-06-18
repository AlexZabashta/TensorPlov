package dataset;

import java.util.function.Function;

import core.DiffFunct;
import core.Result;
import core.TensorShape;
import core.VectorShape;

public class UnPac implements DiffFunct<double[][][], double[]> {
    public final int depth;

    public UnPac(int depth) {
        this.depth = depth;
    }

    @Override
    public VectorShape outputType() {
        return new VectorShape(depth);
    }

    @Override
    public TensorShape inputType() {
        return new TensorShape(1, 1, depth);
    }

    @Override
    public Result<double[][][], double[]> result(double[][][] input) {

        return new Result<double[][][], double[]>(new Function<double[], double[][][]>() {
            @Override
            public double[][][] apply(double[] output) {
                return new double[][][] { { output } };
            }
        }, input[0][0]);
    }

}
