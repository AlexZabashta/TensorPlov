package dev.multidim.symbolic;

import dev.multidim.Dimensional;
import dev.multidim.Node;
import dev.multidim.size.Dim;

public class SymNode extends Node<SymNode> {
    public SymNode(Dim dim, SymNode[] subNodes) {
        super(dim, subNodes);
    }
}
