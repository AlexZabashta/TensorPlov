package scheme;

public class ZeroMean extends Node {

    public final int dim;
    public final Node subNode, offset;
    public final double derivativeFactor;

    public ZeroMean(Node subNode, Node offset, double derivativeFactor) {
        super(subNode, offset);
        this.dim = subNode.outputLength();

        this.subNode = subNode;
        this.offset = offset;
        this.derivativeFactor = derivativeFactor;
    }

    @Override
    public int outputLength() {
        return dim;
    }

}
