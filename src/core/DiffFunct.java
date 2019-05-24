package core;

import java.util.function.Function;

public interface DiffFunct<Domain, Codomain> extends Function<Domain, Codomain> {

    @Override
    public default Codomain apply(Domain input) {
        return result(input).value();
    }

    public Object inputType();

    public Object outputType();

    public Result<Domain, Codomain> result(Domain input);

}
