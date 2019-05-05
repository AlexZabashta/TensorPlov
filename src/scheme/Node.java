package scheme;

import java.util.List;
import java.util.Map;

public interface Node<T extends Node<?>> {
    public int outputLength();

    public int additionalMemory();

    public Variable topologicalSort(List<Node<Variable>> order, Map<Node<?>, Node<Variable>> map, Map<Node<Variable>, Variable> outputs, Map<Node<Variable>, Variable> memory, VariableManager memoryManager);

}
