package dev.arrays;

import java.util.Objects;

public class SubArray<T> {
    public final T array;
    public final int offset;
    public final int length;

    public SubArray(T array, int offset, int length) {
        this.array = Objects.requireNonNull(array);
        this.offset = offset;
        this.length = length;
    }

    public SubArray(T array, int length) {
        this(array, 0, length);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + array.hashCode();
        result = prime * result + length;
        result = prime * result + offset;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        SubArray<T> other = (SubArray<T>) obj;
        if (!array.equals(other.array))
            return false;
        if (length != other.length)
            return false;
        if (offset != other.offset)
            return false;
        return true;
    }

}
