package dev.multidim.dynamic.graph;

import dev.arrays.ArrayProcessor;
import dev.arrays.SubArray;
import dev.multidim.size.Dim;

public class Variable<T> extends DynNode<T> {

    public Variable(SubArray<T> value, SubArray<T> derivative, Dim dim) {
        super(value, derivative, dim);
    }

    @Override
    void propagateDerivative(ArrayProcessor<T> processor) {

    }

}
