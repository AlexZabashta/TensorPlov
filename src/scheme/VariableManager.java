package scheme;

import java.util.Map;

public class VariableManager {

    int offset = 0;

    public int size() {
        return offset;
    }

    public Variable alloc(Map<Node<Variable>, Variable> memory, Node<Variable> node) {
        Variable variable = new Variable("memory", offset, offset += node.outputLength());
        memory.put(node, variable);
        return variable;
    }
}
