package core;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public interface DifferentiableStructure<F, M, T> extends Function<F, T> {
    Pair<M, T> forward(F input);

    F backward(M memory, T deltaOutput);

    @Override
    default T apply(F input) {
        return forward(input).getRight();
    }

}
