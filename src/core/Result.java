package core;

import java.util.function.Function;
import java.util.function.Supplier;

public class Result<Domain, Codomain> implements Function<Codomain, Domain>, Supplier<Codomain> {
    private final Function<Codomain, Domain> derivative;
    private final Codomain value;

    public Result(Function<Codomain, Domain> derivative, Codomain value) {
        this.derivative = derivative;
        this.value = value;
    }

    public Function<Codomain, Domain> derivative() {
        return derivative;
    }

    public Codomain value() {
        return value;
    }

    @Override
    public Domain apply(Codomain deltaOutput) {
        return derivative().apply(deltaOutput);
    }

    @Override
    public Codomain get() {
        return value();
    }

}
