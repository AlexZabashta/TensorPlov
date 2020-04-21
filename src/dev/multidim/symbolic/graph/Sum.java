package dev.multidim.symbolic.graph;

import dev.multidim.size.Dim;
import dev.multidim.symbolic.SymNode;

public class Sum extends SymNode {

    public Sum(Dim dim, SymNode... subNodes) {
        super(dim, subNodes);
    }

}
