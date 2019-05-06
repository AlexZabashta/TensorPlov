package scheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class Node {

    final Node[] subNodes;

    public Node(Node... subNodes) {
        this.subNodes = subNodes;
        for (Node node : subNodes) {
            Objects.requireNonNull(node);
        }
    }

    public abstract int outputLength();

    public Context preCompile() {
        List<Node> order = topologicalSort();
        Map<Node, Variable> f = preCompileForward(order);
        Map<Node, Variable> b = preCompileBackward(order);
        return new Context(order, f, b);
    }

    Map<Node, Variable> preCompileBackward(List<Node> order) {
        Map<Node, Variable> b = new HashMap<>();

        final int size = order.size(), last = size - 1;
        int deltaOffset = 0;
        for (int i = size - 1; i >= 0; i--) {
            Node node = order.get(i);

            if (node instanceof Variable) {
                Variable variable = (Variable) node;
                b.put(node, new Variable("d" + variable.base, variable.from, variable.to));
            } else {
                if (i == last) {
                    b.put(node, new Variable("dy", 0, node.outputLength()));
                } else {
                    b.put(node, new Variable("b", deltaOffset, deltaOffset += node.outputLength()));
                }
            }
        }

        return b;
    }

    Map<Node, Variable> preCompileForward(List<Node> order) {
        Map<Node, Variable> f = new HashMap<>();

        final int size = order.size(), last = size - 1;
        int memoryOffset = 0;
        for (int i = 0; i < size; i++) {
            Node node = order.get(i);
            if (node instanceof Variable) {
                f.put(node, (Variable) node);
            } else {
                if (i == last) {
                    f.put(node, new Variable("y", 0, node.outputLength()));
                } else {
                    f.put(node, new Variable("f", memoryOffset, memoryOffset += node.outputLength()));
                }
            }
        }
        return f;
    }

    public List<Node> topologicalSort() {
        List<Node> order = new ArrayList<>();
        topologicalSort(order, new HashSet<>());
        return order;
    }

    public void topologicalSort(List<Node> order, Set<Node> visited) {
        if (visited.add(this)) {
            for (Node node : subNodes) {
                node.topologicalSort(order, visited);
            }
            order.add(this);
        }
    }
}
