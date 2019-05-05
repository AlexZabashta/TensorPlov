package scheme;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

public class ApplyFunction<SubNode extends Node<?>> implements Node<SubNode> {
    public final int dim;
    public final SubNode node;
    public final DoubleUnaryOperator f, df;

    public ApplyFunction(SubNode node, DoubleUnaryOperator f, DoubleUnaryOperator df) {
        this.dim = node.outputLength();
        this.node = node;

        this.f = f;
        this.df = df;
    }

    @Override
    public int outputLength() {
        return dim;
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

        Variable variable = node.topologicalSort(order, map, outputs, memory, memoryManager);
        Node<Variable> copy = new ApplyFunction<Variable>(variable, f, df);
        order.add(copy);
        Variable output = memoryManager.alloc(memory, copy);
        map.put(this, copy);
        outputs.put(copy, output);
        return output;
    }

}
