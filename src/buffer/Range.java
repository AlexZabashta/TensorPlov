package buffer;

public class Range {
    final int offset, length;

    public static final Range EMPTY = new Range(0);

    public static Range of(int offset, int length) {
        return new Range(offset, length);
    }

    public static Range of(int length) {
        return new Range(length);
    }

    public Range(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public Range(int length) {
        this(0, length);
    }

    public int length() {
        return length;
    }

    public int offset() {
        return offset;
    }

    public int to() {
        return offset + length;
    }

    public Range setLength(int length) {
        return new Range(offset, length);
    }

    public Range setOffset(int offset) {
        return new Range(offset, length);
    }

}
