package dev.multidim;

import java.util.Objects;

import dev.multidim.size.Dim;

public abstract class Node<T> {
    public final Dim dim;
    public final T[] subNodes;

    public Node(Dim dim, T[] subNodes) {
        this.dim = Objects.requireNonNull(dim, "Null dim");
        this.subNodes = Objects.requireNonNull(subNodes, "Null subNodes");
    }
}
