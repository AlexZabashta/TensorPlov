package dev.multidim;

import dev.multidim.size.Dim;

public interface Evaluation<T extends Node<T>> {

    @SuppressWarnings("unchecked")
    default T sum(T... variables) {
        if (variables.length == 0) {
            throw new IllegalArgumentException("Can't determine dimension of empty sum");
        }
        return sum(variables[0].dim, variables);
    }

    @SuppressWarnings("unchecked")
    T sum(Dim dim, T... variables);

}
