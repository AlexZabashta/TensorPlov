package scheme;

import java.util.List;

public class Sigmoid implements StringFunction {

    @Override
    public String backward(String x, String y) {
        return "(1 - (" + y + ")) * (" + y + ")";
    }

    @Override
    public double derivative(double x, double y) {
        return (1 - y) * y;
    }

    @Override
    public double execute(double x) {
        return (1 / (1 + Math.exp(-x)));
    }

    @Override
    public String forward(String x) {
        return "(1 / (1 + exp(-(" + x + "))))";
    }

    @Override
    public void imprt(List<String> list) {
        list.add(" static java.lang.Math.*");
    }

    @Override
    public String toString() {
        return "sigmoid";
    }
}
