package scheme;

import java.util.List;

public class Relu implements StringFunction {

    public final double alpha;

    public Relu(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public String backward(String x, String y) {
        return "((" + x + " < 0) ? " + alpha + " : 1)";
    }

    @Override
    public double derivative(double x, double y) {
        return ((x < 0) ? alpha : 1);
    }

    @Override
    public double execute(double x) {
        return Math.max(alpha * x, x);
    }

    @Override
    public String forward(String x) {
        return "max(" + alpha + " * " + x + ", " + x + ")";
    }

    @Override
    public void imprt(List<String> list) {
        list.add(" static java.lang.Math.*");
    }

    @Override
    public String toString() {
        return "relu";
    }
}
