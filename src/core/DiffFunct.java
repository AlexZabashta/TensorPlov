package core;

import java.util.function.Function;

public interface DiffFunct<Domain, Codomain> extends Function<Domain, Codomain> {

    public Result<Domain, Codomain> result(Domain input);

    @Override
    public default Codomain apply(Domain input) {
        return result(input).value();
    }

}
