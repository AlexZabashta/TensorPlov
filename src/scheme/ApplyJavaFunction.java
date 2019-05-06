package scheme;

import java.util.function.DoubleUnaryOperator;

public class ApplyJavaFunction extends Node {
    public final int dim;
    public final Node subNode;
    public final DoubleUnaryOperator f, df;

    public ApplyJavaFunction(Node subNode, DoubleUnaryOperator f, DoubleUnaryOperator df) {
        super(subNode);
        this.dim = subNode.outputLength();
        this.subNode = subNode;

        this.f = f;
        this.df = df;
    }

    @Override
    public int outputLength() {
        return dim;
    }

    

}
