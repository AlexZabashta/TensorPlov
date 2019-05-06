package scheme;

public class ApplyFunction extends Node {
    public final int dim;
    public final Node subNode;
    public final StringFunction function;

    public ApplyFunction(Node subNode, StringFunction function) {
        super(subNode);
        this.dim = subNode.outputLength();
        this.subNode = subNode;

        this.function = function;
    }

    @Override
    public int outputLength() {
        return dim;
    }

}
