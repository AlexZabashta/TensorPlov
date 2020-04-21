package dev.multidim.dynamic.graph;

import java.util.Objects;

import dev.arrays.ArrayProcessor;
import dev.arrays.SubArray;
import dev.multidim.Dimensional;
import dev.multidim.Node;
import dev.multidim.size.Dim;

public abstract class DynNode<T> extends Node<DynNode<T>> {
    public final SubArray<T> value, derivative;
    public final int length;

    @SafeVarargs
    public DynNode(SubArray<T> value, SubArray<T> derivative, Dim dim, DynNode<T>... subNodes) {
        super(dim, subNodes);
        this.value = Objects.requireNonNull(value, "Null value");
        this.derivative = Objects.requireNonNull(derivative, "Null derivative");
        this.length = dim.size();
        if (length < 0) {
            throw new IllegalArgumentException("Undefined size");
        }

        if (value.length != this.length) {
            throw new IllegalArgumentException("value.length != this.length");
        }

        if (derivative.length != this.length) {
            throw new IllegalArgumentException("derivative.length != this.length");
        }
    }

    abstract void propagateDerivative(ArrayProcessor<T> processor);

}
