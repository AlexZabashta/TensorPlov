package dev.graph;

import dev.dynamic.Node;

public interface IBuilder<T> {
	@SuppressWarnings("unchecked")
	Node<T> sum(Node<T>... variables);
}
