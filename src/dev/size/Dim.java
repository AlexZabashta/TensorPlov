package dev.size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

public abstract class Dim implements IntSupplier {

    public static final Dim EMPTY = new Val(0);
    public static final Dim UNIT = new Val(1);

    public static Val[] convert(int[] numbers) {
        Val[] dims = new Val[numbers.length];
        for (int i = 0; i < dims.length; i++) {
            dims[i] = new Val(numbers[i]);
        }
        return dims;
    }

    public static boolean equalSize(int size1, int size2) {
        if (size1 < 0) {
            return size2 < 0;
        }
        return size1 == size2;
    }

    public abstract void appendTo(Appendable appendable);

    @Override
    public int getAsInt() {
        return size();
    }

    public List<List<Dim>> listNormalForm() {
        int size = size();
        if (size == 0) {
            return new ArrayList<>();
        }
        if (size == 1) {
            return Arrays.asList(Arrays.asList(Dim.UNIT));
        }
        return Arrays.asList(Arrays.asList(this));
    }

    public abstract int size();

    public int size(Map<Var, Integer> map) {
        return size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendTo(builder);
        return builder.toString();
    }

    public boolean variable() {
        return size() < 0;
    }
}
