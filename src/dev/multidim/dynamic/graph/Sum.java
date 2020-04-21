package dev.multidim.dynamic.graph;

import dev.arrays.ArrayProcessor;
import dev.arrays.SubArray;
import dev.multidim.size.Dim;

public class Sum<T> extends DynNode<T> {

    @SafeVarargs
    public Sum(SubArray<T> value, SubArray<T> derivative, Dim dim, DynNode<T>... subNodes) {
        super(value, derivative, dim, subNodes);
    }

    @Override
    void propagateDerivative(ArrayProcessor<T> processor) {
        for (DynNode<T> node : subNodes) {
            processor.addTo(derivative, node.derivative);
        }
    }

}
