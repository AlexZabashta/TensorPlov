package scheme;

public class MemoryManager {
    public final String base;
    int offset;

    public MemoryManager(String base) {
        this.base = base;
    }

    public Variable alloc(int size) {
        return new Variable(base, offset, offset += size);
    }
}
