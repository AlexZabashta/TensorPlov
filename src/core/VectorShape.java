package core;

public class VectorShape extends TensorShape {
    public static VectorShape add(VectorShape first, VectorShape secnd) {
        return new VectorShape(first.length + secnd.length);
    }

    public final int length;

    public VectorShape(int length) {
        super(length);
        this.length = length;
    }

    public VectorShape add(VectorShape shape) {
        return add(this, shape);
    }

}
