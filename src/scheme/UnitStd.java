package scheme;

public class UnitStd extends Node {

    public final int dim;
    public final Node subNode, scale;
    public final double derivativeFactor;

    public UnitStd(Node subNode, Node scale, double derivativeFactor) {
        super(subNode, scale);
        this.dim = subNode.outputLength();

        this.subNode = subNode;
        this.scale = scale;
        this.derivativeFactor = derivativeFactor;
    }

    @Override
    public int outputLength() {
        return dim;
    }

}
