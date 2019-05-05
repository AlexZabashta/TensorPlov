package scheme;

import java.util.List;
import java.util.Map;

public class Variable implements Node<Variable> {
    public final String base;

    public Variable(String base, int from, int to) {
        this.base = base;
        this.from = from;
        this.to = to;
    }

    public final int from;
    public final int to;

    @Override
    public int outputLength() {
        return to - from;
    }

    @Override
    public int additionalMemory() {
        return 0;
    }

    @Override
    public Variable topologicalSort(List<Node<Variable>> order, Map<Node<?>, Node<Variable>> map, Map<Node<Variable>, Variable> outputs, Map<Node<Variable>, Variable> memory, VariableManager memoryManager) {
        return this;
    }
}
