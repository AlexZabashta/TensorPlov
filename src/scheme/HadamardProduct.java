package scheme;

public class HadamardProduct extends Node {
    public final int dim;
    public final Node left, right;

    public HadamardProduct(Node left, Node right) {
        super(left, right);
        this.dim = left.outputLength();

        if (right.outputLength() != dim) {
            throw new IllegalArgumentException("left.outputLength() != right.outputLength()");
        }

        this.left = left;
        this.right = right;
    }

    @Override
    public int outputLength() {
        return dim;
    }
}
