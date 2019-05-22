package scheme;

public class Builder {
    public static Node buildLayers(boolean lastLinear, int... sizes) {
        int offset = 0;
        Node node = new Variable("x", 0, sizes[0]);
        StringFunction relu = new Relu(0.001);
        StringFunction tanh = new Tanh();

        // node = new UnitDerivative(node);

        for (int i = 1; i < sizes.length; i++) {
            // if (i == 1) {
            // Variable sigma = new Variable("w", offset, offset += sizes[0]);
            // node = new UnitStd(node, sigma, 0.01);
            // } else {
            // Variable mu = new Variable("w", offset, offset += sizes[i - 1]);
            // node = new ZeroMean(node, mu, 0.01);
            // }

            Variable w = new Variable("w", offset, offset += sizes[i - 1] * sizes[i]);
            Multiplication mul = new Multiplication(1, sizes[i - 1], sizes[i], node, w);

            if (lastLinear) {
                if (i < sizes.length - 2) {
                    Variable b = new Variable("w", offset, offset += sizes[i]);
                    Sum sum = new Sum(mul, b);
                    node = new ApplyFunction(sum, relu);
                }
                if (i == sizes.length - 2) {
                    Variable b = new Variable("w", offset, offset += sizes[i]);
                    Sum sum = new Sum(mul, b);
                    node = new ApplyFunction(sum, tanh);
                }

                if (i == sizes.length - 1) {
                    node = mul;
                }
            } else {
                Variable b = new Variable("w", offset, offset += sizes[i]);
                Sum sum = new Sum(mul, b);
                if (i == sizes.length - 1) {
                    node = new ApplyFunction(sum, tanh);
                } else {
                    node = new ApplyFunction(sum, relu);
                }
            }
        }

        return node;
    }

    public static Node fullConnectedLayer(MemoryManager mem, Node input, int outputLength, StringFunction activation) {
        Variable w = mem.alloc(input.outputLength() * outputLength);
        Multiplication mul = new Multiplication(1, input.outputLength(), outputLength, input, w);
        Variable b = mem.alloc(outputLength);
        Sum sum = new Sum(mul, b);
        return new ApplyFunction(sum, activation);

    }

    public static Node conLstmLayer(MemoryManager mem, int size) {

        Variable h1 = new Variable("x", 0, size);
        Variable c1 = new Variable("x", h1.to, h1.to + size);
        Variable h2 = new Variable("x", c1.to, c1.to + size);
        Variable c2 = new Variable("x", h2.to, h2.to + size);

        Node hh = new Concat(h1, h2);

        Tanh tanh = new Tanh();
        Sigmoid sigmoid = new Sigmoid();

        Node f1 = fullConnectedLayer(mem, hh, size, sigmoid);
        Node f2 = fullConnectedLayer(mem, hh, size, sigmoid);
        Node i = fullConnectedLayer(mem, hh, size, sigmoid);
        Node s = fullConnectedLayer(mem, hh, size, tanh);
        Node o = fullConnectedLayer(mem, hh, size, sigmoid);
        Node c = new Sum(new Sum(new HadamardProduct(f1, c1), new HadamardProduct(f2, c2)), new HadamardProduct(i, s));
        Node h = new HadamardProduct(o, new ApplyFunction(c, tanh));

        return new Concat(h, c);
    }

    public static Node decLstmLayer(MemoryManager mem, int size) {
        Tanh tanh = new Tanh();
        Sigmoid sigmoid = new Sigmoid();

        Variable h = new Variable("x", 0, size);
        Variable c = new Variable("x", h.to, h.to + size);

        Node s = fullConnectedLayer(mem, h, size, tanh);

        Node f1 = fullConnectedLayer(mem, h, size, sigmoid);
        Node i1 = fullConnectedLayer(mem, h, size, sigmoid);
        Node o1 = fullConnectedLayer(mem, h, size, sigmoid);
        Node c1 = new Sum(new HadamardProduct(f1, c), new HadamardProduct(i1, s));
        Node h1 = new HadamardProduct(o1, new ApplyFunction(c1, tanh));

        Node f2 = fullConnectedLayer(mem, h, size, sigmoid);
        Node i2 = fullConnectedLayer(mem, h, size, sigmoid);
        Node o2 = fullConnectedLayer(mem, h, size, sigmoid);
        Node c2 = new Sum(new HadamardProduct(f2, c), new HadamardProduct(i2, s));
        Node h2 = new HadamardProduct(o2, new ApplyFunction(c2, tanh));

        return new Concat(h1, c1, h2, c2);
    }
}
