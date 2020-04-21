package dev.size;

import java.io.IOException;

public class Val extends Dim {

    final int value;

    public Val(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value = " + value + " can't be negative");
        }
        this.value = value;
    }

    @Override
    public void appendTo(Appendable appendable) {
        try {
            appendable.append(Integer.toString(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        return value;
    }

    @Override
    public boolean variable() {
        return false;
    }

}
