package dev.graph;

import dev.dynamic.ArrayProcessor;
import dev.dynamic.Node;
import dev.dynamic.Sum;
import dev.size.Dim;

public class Builder<T> implements IBuilder<T> {

	ArrayProcessor<T> processor;

	public Builder(ArrayProcessor<T> processor) {
		this.processor = processor;
	}

	@SafeVarargs
	@Override
	public final Node<T> sum(Node<T>... nodes) {
		Dim dim = nodes[0].dim;
		int length = dim.size();
		T result = processor.alloc(length, 0.0);

		for (Node<T> node : nodes) {
			processor.add(node.result, node.resPos, result, 0, length);
		}

		return new Sum<T>(result, 0, dim, nodes);

	}
}
