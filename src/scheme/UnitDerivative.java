package scheme;

public class UnitDerivative extends Node {

    public final int dim;
    public final Node subNode;

    public UnitDerivative(Node subNode) {
        super(subNode);
        this.dim = subNode.outputLength();
        this.subNode = subNode;
    }

    @Override
    public int outputLength() {
        return dim;
    }

}
