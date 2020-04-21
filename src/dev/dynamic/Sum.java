package dev.dynamic;

import dev.size.Dim;

public class Sum<T> extends Node<T> {

	@SafeVarargs
	public Sum(T result, int resPos, Dim dim, Node<T>... subNodes) {
		super(result, resPos, dim, subNodes);
	}

	@Override
	void propagateDerivative(ArrayProcessor<T> processor) {
		// TODO Auto-generated method stub

	}

}
