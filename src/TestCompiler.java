import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import scheme.ApplyFunction;
import scheme.Multiplication;
import scheme.StringFunction;
import scheme.Sum;
import scheme.Variable;
import tojava.CompilerToSrc;

public class TestCompiler {
    public static void main(String[] args) throws FileNotFoundException {
        int n = 3, m = 7, k = 5;

        Variable w = new Variable("w", 0, n * m);
        Variable x = new Variable("x", 0, m * k);
        Variable b = new Variable("w", n * m, n * m + k * n);

        Multiplication mul = new Multiplication(n, m, k, w, x);

        // System.out.println(mul.outputLength() + " " + b.outputLength());
        Sum sum = new Sum(mul, b);

        ApplyFunction act = new ApplyFunction(sum, new StringFunction() {

            @Override
            public void imprt(List<String> list) {
                list.add(" static java.lang.Math.*");
            }

            @Override
            public String forward(String x) {
                return "tanh(" + x + ")";
            }

            @Override
            public double execute(double x) {
                return Math.tan(x);
            }

            @Override
            public double derivative(double x, double y) {
                return 1 - y * y;
            }

            @Override
            public String backward(String x, String y) {
                return "(1 - " + y + " * " + y + ")";
            }
        });

        String name = "DiffFun";

        List<String> programm = CompilerToSrc.compile(null, name, act.preCompile());

        try (PrintWriter writer = new PrintWriter(name + ".java")) {
            for (String line : programm) {
                writer.println(line);
            }
        }

    }
}
