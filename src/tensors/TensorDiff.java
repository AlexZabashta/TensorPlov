package tensors;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffStruct;

public class TensorDiff<M> implements DiffStruct<double[], M, double[]> {

    final int[] inputShape;
    final int[] outputShape;

    final int inputSize;
    final int outpuSize;

    public static int size(int... shape) {
        int product = 1;
        for (int len : shape) {
            if (len < 1) {
                throw new IllegalArgumentException("dim < 1");
            }
            product *= len;
        }
        return product;
    }

    public TensorDiff(int[] inputShape, int[] outputShape) {
        this.inputSize = size(inputShape);
        this.inputShape = inputShape.clone();
        this.outpuSize = size(outputShape);
        this.outputShape = outputShape.clone();
    }

    @Override
    public Pair<M, double[]> forward(double[] input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double[] backward(M memory, double[] deltaOutput) {
        // TODO Auto-generated method stub
        return null;
    }

}
