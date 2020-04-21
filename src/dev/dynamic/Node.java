package dev.dynamic;

import dev.size.Dim;

public abstract class Node<T> {
	T derivative;
	int derPos;

	public final Dim dim;
	final int length;

	public final int resPos;
	public final T result;
	public final Node<T>[] subNodes;

	@SafeVarargs
	public Node(T result, int resPos, Dim dim, Node<T>... subNodes) {
		this.result = result;
		this.resPos = resPos;
		this.dim = dim;
		this.length = dim.size();
		if (length < 0) {
			throw new IllegalArgumentException("Undefined size");
		}
		this.subNodes = subNodes;
	}

	abstract void propagateDerivative(ArrayProcessor<T> processor);

	void setDerivative(T derivative, int derPos, ArrayProcessor<T> processor) {
		this.derivative = derivative;
		this.derPos = derPos;
		propagateDerivative(processor);
	}
}
