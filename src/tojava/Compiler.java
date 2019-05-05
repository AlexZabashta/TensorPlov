package tojava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import scheme.Node;
import scheme.Variable;

public class Compiler {
    public static Object compile(Node root) {
        List<Node> order = new ArrayList<>();
        root.topologicalSort(order, new HashSet<>());

        int memoryOffset = 0;

        Map<Node, Variable> nodeOutput = new HashMap<>();
        Map<Node, Variable> nodeMemory = new HashMap<>();

        for (Node node : order) {

        }

        return null;
    }
}
