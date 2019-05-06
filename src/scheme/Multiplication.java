package scheme;

public class Multiplication extends Node {

    public final int lftDim, midDim, rgtDim;
    public final Node left, right;

    public Multiplication(int lftDim, int midDim, int rgtDim, Node left, Node right) {
        super(left, right);
        this.lftDim = lftDim;
        this.midDim = midDim;
        this.rgtDim = rgtDim;

        if (left.outputLength() != lftDim * midDim) {
            throw new IllegalArgumentException("left.outputLength() != lftDim * midDim");
        }

        if (right.outputLength() != midDim * rgtDim) {
            throw new IllegalArgumentException("right.outputLength() != midDim * rgtDim");
        }

        this.left = left;
        this.right = right;
    }

    @Override
    public int outputLength() {
        return lftDim * rgtDim;
    }

    

}
