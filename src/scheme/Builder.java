package scheme;

public class Builder {
    public static Node buildLayers(boolean lastLinear, int... sizes) {
        int offset = 0;
        Node node = new Variable("x", 0, sizes[0]);
        StringFunction relu = new Relu(0.001);
        StringFunction tanh = new Tanh();

        node = new UnitDerivative(node);

        for (int i = 1; i < sizes.length; i++) {
            if (i == 1) {
                Variable sigma = new Variable("w", offset, offset += sizes[0]);
                node = new UnitStd(node, sigma, 0.01);
            } else {
                Variable mu = new Variable("w", offset, offset += sizes[i - 1]);
                node = new ZeroMean(node, mu, 0.01);
            }

            Variable w = new Variable("w", offset, offset += sizes[i - 1] * sizes[i]);
            Multiplication mul = new Multiplication(1, sizes[i - 1], sizes[i], node, w);
            Variable b = new Variable("w", offset, offset += sizes[i]);

            Sum sum = new Sum(mul, b);

            if (lastLinear) {
                if (i < sizes.length - 2) {
                    node = new ApplyFunction(sum, relu);
                }
                if (i == sizes.length - 2) {
                    node = new ApplyFunction(sum, tanh);
                }

                if (i == sizes.length - 1) {
                    node = sum;
                }
            } else {
                if (i == sizes.length - 1) {
                    node = new ApplyFunction(sum, tanh);
                } else {
                    node = new ApplyFunction(sum, relu);
                }
            }
        }

        return node;
    }
}
