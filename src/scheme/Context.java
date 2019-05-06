package scheme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    public final List<Node> order;
    public final Map<Node, Variable> f, b;
    public final Map<String, Integer> variableSize = new HashMap<>();

    void extractMax(Map<Node, Variable> map) {
        for (Variable variable : map.values()) {
            String base = variable.base;
            variableSize.put(base, Math.max(variableSize.getOrDefault(base, 0), variable.to));
        }
    }

    public Context(List<Node> order, Map<Node, Variable> f, Map<Node, Variable> b) {
        this.order = order;
        this.f = f;
        this.b = b;
        extractMax(f);
        extractMax(b);

    }
}
