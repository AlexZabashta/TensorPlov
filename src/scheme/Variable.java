package scheme;

public class Variable extends Node {
    public final String base;

    public final int from, to;

    public Variable(String base, int from, int to) {
        super();
        this.base = base;
        this.from = from;
        this.to = to;
    }

    @Override
    public int outputLength() {
        return to - from;
    }

}
