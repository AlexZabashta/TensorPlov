package core;

import java.util.Arrays;

public class TensorShape {
    final int[] dimensions;

    public TensorShape(int... dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {
        return Arrays.toString(dimensions);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dimensions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TensorShape shape = (TensorShape) obj;
        return Arrays.equals(dimensions, shape.dimensions);
    }

}
