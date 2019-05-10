package scheme;

import java.util.List;

public class Tanh implements StringFunction {

    @Override
    public String backward(String x, String y) {
        return "(1 - " + y + " * " + y + ")";
    }

    @Override
    public double derivative(double x, double y) {
        return 1 - y * y;
    }

    @Override
    public double execute(double x) {
        return Math.tanh(x);
    }

    @Override
    public String forward(String x) {
        return "tanh(" + x + ")";
    }

    @Override
    public void imprt(List<String> list) {
        list.add(" static java.lang.Math.*");
    }

    @Override
    public String toString() {
        return "tanh";
    }
}
