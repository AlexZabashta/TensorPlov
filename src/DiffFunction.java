import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public interface DiffFunction<F, T> {
    public Pair<Function<T, F>, T> forward(F input);    
}
