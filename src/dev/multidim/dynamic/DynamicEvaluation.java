package dev.multidim.dynamic;

import dev.arrays.ArrayProcessor;
import dev.arrays.SubArray;
import dev.multidim.Evaluation;
import dev.multidim.dynamic.graph.DynNode;
import dev.multidim.dynamic.graph.Sum;
import dev.multidim.dynamic.graph.Variable;
import dev.multidim.size.Dim;

public class DynamicEvaluation<T> implements Evaluation<DynNode<T>> {

    ArrayProcessor<T> processor;

    public DynamicEvaluation(ArrayProcessor<T> processor) {
        this.processor = processor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Sum<T> sum(DynNode<T>... nodes) {
        Dim dim = nodes[0].dim;
        int length = dim.size();

        SubArray<T> value = new SubArray<T>(processor.alloc(length, 0.0), 0, length);
        SubArray<T> derivative = new SubArray<T>(processor.alloc(length, 0.0), 0, length);
        for (DynNode<T> node : nodes) {
            processor.addTo(node.value, value);
        }
        return new Sum<>(value, derivative, dim, nodes);
    }

    public Variable<T> variable(Dim dim, SubArray<T> value) {
        SubArray<T> derivative = processor.allocSubArray(dim.size());
        return new Variable<>(value, derivative, dim);
    }

    @Override
    public DynNode<T> sum(Dim dim, DynNode<T>... variables) {
        // TODO Auto-generated method stub
        return null;
    }

}
