package scheme;

import java.util.List;
import java.util.Map;

public class DotProduct<SubNode extends Node<?>> implements Node<SubNode> {
    public final int dim;
    public final SubNode left, right;

    public DotProduct(SubNode left, SubNode right) {
        this.dim = left.outputLength();

        if (right.outputLength() != dim) {
            throw new IllegalArgumentException("left.outputLength() != right.outputLength()");
        }

        this.left = left;
        this.right = right;
    }

    @Override
    public int outputLength() {
        return 1;
    }

    @Override
    public int additionalMemory() {
        return 0;
    }

    @Override
    public Variable topologicalSort(List<Node<Variable>> order, Map<Node<?>, Node<Variable>> map, Map<Node<Variable>, Variable> outputs, Map<Node<Variable>, Variable> memory, VariableManager memoryManager) {
        if (map.containsKey(this)) {
            return outputs.get(map.get(this));
        }

        Variable lvar = left.topologicalSort(order, map, outputs, memory, memoryManager);
        Variable rvar = right.topologicalSort(order, map, outputs, memory, memoryManager);
        Node<Variable> copy = new DotProduct<Variable>(lvar, rvar);
        order.add(copy);
        Variable output = memoryManager.alloc(memory, copy);
        map.put(this, copy);
        outputs.put(copy, output);
        return output;
    }

}
