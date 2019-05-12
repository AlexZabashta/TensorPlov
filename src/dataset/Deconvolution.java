package dataset;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffStruct;

public class Deconvolution implements DiffStruct<double[], Deconvolution.Memory, double[][][]> {

    class Memory {

    }

    @Override
    public Pair<Memory, double[][][]> forward(double[] input) {
        return null;
    }

    @Override
    public double[] backward(Memory memory, double[][][] deltaOutput) {
        return null;
    }

    @Override
    public Class<double[]> inputClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Memory> memoryClass() {
        // TODO Auto-generated method stub
        return null;
    }

}
