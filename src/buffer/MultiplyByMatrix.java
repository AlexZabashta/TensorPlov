package buffer;

import org.apache.commons.lang3.tuple.Pair;

public class MultiplyByMatrix extends Transform<MultiplyByMatrix> {

    public MultiplyByMatrix(int inputLength, int outputLength) {
        super(Range.of(inputLength), Range.EMPTY, Range.of(inputLength, outputLength), Range.of(inputLength * outputLength), Range.of(inputLength), Range.of(inputLength, outputLength), Range.of(inputLength * outputLength));
    }

    @Override
    public void backward(double[] values, double[] weights, double[] deltaValues, double[] deltaWeights) {
        // TODO Auto-generated method stub

    }

    @Override
    public MultiplyByMatrix copy(Range input, Range hidden, Range output, Range weights, Range deltaInput, Range deltaOutput, Range deltaWeights) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void forward(double[] values, double[] weights) {
        // TODO Auto-generated method stub

    }

    @Override
    public Class<Pair<double[], double[]>> inputClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Pair<double[], double[]>> memoryClass() {
        // TODO Auto-generated method stub
        return null;
    }

}
