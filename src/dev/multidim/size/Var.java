package dev.multidim.size;

import java.io.IOException;
import java.util.Map;

public class Var extends Dim {

    String name;

    public Var(String name) {
        this.name = name;
    }

    @Override
    public void appendTo(Appendable appendable) {
        try {
            appendable.append(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        return -1;
    }

    @Override
    public int size(Map<Var, Integer> map) {
        int size = map.get(this);
        if (size < 0) {
            throw new IllegalArgumentException(name + " size = " + size + " can't be negative");
        }
        return size;
    }

    @Override
    public boolean variable() {
        return true;
    }

}
