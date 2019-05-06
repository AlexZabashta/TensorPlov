package buffer;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffStruct;

public abstract class Transform<TransformType extends Transform<TransformType>> implements DiffStruct<Pair<double[], double[]>, Pair<double[], double[]>, double[]> {

    public final Range input, hidden, output, weights, deltaInput, deltaOutput, deltaWeights;

    public Transform(Range input, Range hidden, Range output, Range weights, Range deltaInput, Range deltaOutput, Range deltaWeights) {
        this.input = input;
        this.hidden = hidden;
        this.output = output;
        this.weights = weights;
        this.deltaInput = deltaInput;
        this.deltaOutput = deltaOutput;
        this.deltaWeights = deltaWeights;
    }

    public abstract void backward(double[] values, double[] weights, double[] deltaValues, double[] deltaWeights);

    @Override
    public Pair<double[], double[]> backward(Pair<double[], double[]> memory, double[] dy) {
        double[] w = memory.getLeft();
        double[] buffer = memory.getRight();

        double[] deltaW = new double[deltaWeights.to()];
        double[] deltaV = new double[deltaInput.to()];

        backward(buffer, w, deltaV, deltaW);

        double[] dw = new double[weights.length];
        double[] dx = new double[input.length];

        System.arraycopy(deltaV, deltaInput.offset, dx, 0, deltaInput.length);
        System.arraycopy(deltaW, deltaWeights.offset, dw, 0, deltaWeights.length);

        return Pair.of(dx, dw);
    }

    public abstract TransformType copy(Range input, Range hidden, Range output, Range weights, Range deltaInput, Range deltaOutput, Range deltaWeights);

    public abstract void forward(double[] values, double[] weights);

    @Override
    public Pair<Pair<double[], double[]>, double[]> forward(Pair<double[], double[]> xw) {
        double[] x = xw.getLeft();
        double[] w = xw.getRight();

        double[] buffer = new double[Math.max(input.to(), Math.max(hidden.to(), output.to()))];
        System.arraycopy(x, 0, buffer, input.offset(), input.length);

        forward(buffer, w);

        double[] y = new double[output.length];
        System.arraycopy(buffer, output.offset, y, 0, output.length);

        return Pair.of(Pair.of(w, buffer), y);
    }

    public Transform<TransformType> setDeltaInput(Range deltaInput) {
        return copy(input, hidden, output, weights, deltaInput, deltaOutput, deltaWeights);
    }

    public Transform<TransformType> setDeltaOutput(Range deltaOutput) {
        return copy(input, hidden, output, weights, deltaInput, deltaOutput, deltaWeights);
    }

    public Transform<TransformType> setDeltaWeights(Range deltaWeights) {
        return copy(input, hidden, output, weights, deltaInput, deltaOutput, deltaWeights);
    }

    public Transform<TransformType> setHidden(Range hidden) {
        return copy(input, hidden, output, weights, deltaInput, deltaOutput, deltaWeights);
    }

    public Transform<TransformType> setInput(Range input) {
        return copy(input, hidden, output, weights, deltaInput, deltaOutput, deltaWeights);
    }

    public Transform<TransformType> setOutput(Range output) {
        return copy(input, hidden, output, weights, deltaInput, deltaOutput, deltaWeights);
    }

    public Transform<TransformType> setWeights(Range weights) {
        return copy(input, hidden, output, weights, deltaInput, deltaOutput, deltaWeights);
    }

}
