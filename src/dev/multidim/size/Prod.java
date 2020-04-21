package dev.multidim.size;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Prod extends Dim {

    public static Dim[] foldAssociative(Dim... dims) {
        List<Dim> list = new ArrayList<Dim>(dims.length);
        for (Dim dim : dims) {
            Objects.requireNonNull(dim, "Sub dim can't be null");
            if (dim instanceof Prod) {
                list.addAll(Arrays.asList(((Prod) dim).dims));
            }
        }
        return list.toArray(new Dim[list.size()]);
    }

    public static int size(Dim... dims) {
        int size = 1;
        for (Dim dim : dims) {
            int ds = dim.size();
            if (ds < 0) {
                size = -1;
                continue;
            }
            if (ds == 0) {
                return 0;
            }
            size *= ds;
        }
        return size;
    }

    final Dim[] dims;
    final int size, hashCode;

    public Prod(Dim... dims) {
        this.dims = dims;
        this.size = size(dims);
        this.hashCode = Arrays.hashCode(dims);
    }

    public Prod(int... dims) {
        this(Dim.convert(dims));
    }

    @Override
    public void appendTo(Appendable appendable) {
        try {
            appendable.append('[');
            boolean sep = false;
            for (Dim dim : dims) {
                if (sep) {
                    appendable.append(',');
                    appendable.append(' ');
                }
                dim.appendTo(appendable);
                sep = true;
            }
            appendable.append(']');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Prod other = (Prod) obj;
        if (hashCode != other.hashCode)
            return false;
        if (!Dim.equalSize(size, other.size))
            return false;
        if (!Arrays.equals(dims, other.dims))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public List<List<Dim>> listNormalForm() {
        if (size == 0) {
            return new ArrayList<>();
        }
        if (size == 1) {
            return Arrays.asList(Arrays.asList(Dim.UNIT));
        }

        List<List<Dim>> prod = new ArrayList<>();
        prod.add(new ArrayList<>());

        for (Dim dim : dims) {
            if (dim.size() == 1) {
                continue;
            }
            
            
            
        }
        return prod;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int size(Map<Var, Integer> map) {
        int size = 1;
        for (Dim dim : dims) {
            size *= dim.size(map);
        }
        return size;
    }

}
