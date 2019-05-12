package core;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public interface DiffStruct<F, M, T> extends Function<F, T> {
    public Pair<M, T> forward(F input);

    public F backward(M memory, T deltaOutput);

    @Override
    public default T apply(F input) {
        return forward(input).getRight();
    }

    public Class<F> inputClass();
    public Class<M> memoryClass();

}
