import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import scheme.ApplyFunction;
import scheme.Multiplication;
import scheme.Node;
import scheme.Normalization;
import scheme.StringFunction;
import scheme.Sum;
import scheme.Tanh;
import scheme.Variable;
import tojava.CompilerToSrc;

public class TestCompiler {

    public static void main(String[] args) throws FileNotFoundException {

        int offset = 0;

        int[] layers = { 784, 183, 42, 10 };
        Node node = new Variable("x", 0, layers[0]);
        StringFunction tanh = new Tanh();

        for (int i = 1; i < layers.length; i++) {
            Variable mu = new Variable("w", offset, offset += layers[i - 1]);
            Variable sigma = new Variable("w", offset, offset += layers[i - 1]);

            node = new Normalization(node, mu, sigma);

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
