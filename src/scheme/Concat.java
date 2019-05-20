package scheme;

public class Concat extends Node {

    public final int length;

    public Concat(Node... subNodes) {
        super(subNodes);
        int sum = 0;
        for (Node node : subNodes) {
            sum += node.outputLength();
        }
        this.length = sum;
    }

    @Override
    public int outputLength() {
        return length;
    }

}
