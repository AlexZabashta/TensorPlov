package core;

import java.util.function.Function;

public class WrapVector implements DiffFunct<double[], double[]> {

    public final int length;

    public WrapVector(int length) {
        this.length = length;
    }

    @Override
    public VectorShape inputType() {
        return new VectorShape(length);
    }

    @Override
    public VectorShape outputType() {
        return new VectorShape(length);
    }

    @Override
    public Result<double[], double[]> result(double[] input) {
        return new Result<>(new Function<double[], double[]>() {
            @Override
            public double[] apply(double[] delta) {
                return delta;
            }
        }, input);
    }

}
