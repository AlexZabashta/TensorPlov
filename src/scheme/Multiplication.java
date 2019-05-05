package scheme;

import java.util.List;
import java.util.Map;

public class Multiplication<SubNode extends Node<?>> implements Node<SubNode> {

    public final int lftDim, midDim, rgtDim;
    public final SubNode left, right;

    public Multiplication(int lftDim, int midDim, int rgtDim, SubNode left, SubNode right) {
        this.lftDim = lftDim;
        this.midDim = midDim;
        this.rgtDim = rgtDim;

        if (left.outputLength() != lftDim * midDim) {
            throw new IllegalArgumentException("left.outputLength() != lftDim * midDim");
        }

        if (right.outputLength() != midDim * rgtDim) {
            throw new IllegalArgumentException("right.outputLength() != midDim * rgtDim");
        }

        this.left = left;
        this.right = right;
    }

    @Override
    public int outputLength() {
        return lftDim * rgtDim;
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
        Node<Variable> copy = new Multiplication<Variable>(lftDim, midDim, rgtDim, lvar, rvar);
        order.add(copy);
        Variable output = memoryManager.alloc(memory, copy);
        map.put(this, copy);
        outputs.put(copy, output);
        return output;
    }
}
