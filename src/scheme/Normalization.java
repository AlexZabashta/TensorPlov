package scheme;

public class Normalization extends Node {

    public final int dim;
    public final Node subNode, offset, scale;

    public Normalization(Node subNode, Node offset, Node scale) {
        super(subNode, offset, scale);
        this.dim = subNode.outputLength();

        this.subNode = subNode;
        this.scale = scale;
        this.offset = offset;
    }

    @Override
    public int outputLength() {
        return dim;
    }

}
