import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import scheme.ApplyFunction;
import scheme.Builder;
import scheme.MemoryManager;
import scheme.Multiplication;
import scheme.Node;
import scheme.UnitDerivative;
import scheme.UnitStd;
import scheme.StringFunction;
import scheme.Sum;
import scheme.Tanh;
import scheme.Variable;
import scheme.ZeroMean;
import tojava.CompilerToSrc;

public class TestCompiler {

    public static void main(String[] args) throws FileNotFoundException {

        MemoryManager manager = new MemoryManager("w");

        Node node = Builder.doubleLstmLayer(manager, 20);

        String name = "LSTM";

        List<String> programm = CompilerToSrc.compile(null, name, node.preCompile());

        try (PrintWriter writer = new PrintWriter(name + ".java")) {
            for (String line : programm) {
                writer.println(line);
            }
        }

    }

    static void testFC() throws FileNotFoundException {
        int offset = 0;

        int[] layers = { 784, 183, 42, 10 };
        Node node = new Variable("x", 0, layers[0]);
        StringFunction tanh = new Tanh();

        for (int i = 1; i < layers.length; i++) {

            Variable mu = new Variable("w", offset, offset += layers[i - 1]);
            node = new ZeroMean(node, mu, 0.01);

            Variable sigma = new Variable("w", offset, offset += layers[i - 1]);
            node = new UnitStd(node, sigma, 0.01);

            node = new UnitDerivative(node);

            Variable w = new Variable("w", offset, offset += layers[i - 1] * layers[i]);
            Multiplication mul = new Multiplication(1, layers[i - 1], layers[i], node, w);
            Variable b = new Variable("w", offset, offset += layers[i]);

            Sum sum = new Sum(mul, b);
            ApplyFunction act = new ApplyFunction(sum, tanh);

            node = act;
        }

        String name = "DiffFun";

        List<String> programm = CompilerToSrc.compile(null, name, node.preCompile());

        try (PrintWriter writer = new PrintWriter(name + ".java")) {
            for (String line : programm) {
                writer.println(line);
            }
        }
    }
}
