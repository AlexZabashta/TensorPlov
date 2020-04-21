package dev.dynamic;

import dev.size.Dim;

public class Variable<T> extends Node<T> {

	public Variable(T result, int resPos, Dim dim) {
		super(result, resPos, dim);
	}

	@Override
	void propagateDerivative(ArrayProcessor<T> processor) {

	}

}
