package scheme;

import java.util.List;

public interface StringFunction {
    public String forward(String x);

    public String backward(String x, String y);

    public double execute(double x);

    public double derivative(double x, double y);

    public void imprt(List<String> list);

}
