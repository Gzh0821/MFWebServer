package cn.monkey.gateway.stream.blacklist.async;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CompositeBlackKeyResolverBuilder {
    private CompositeBlackKeyResolverBuilder() {
        creators = new ArrayList<>();
    }

    static class NoopBlackKeyResolver implements BlackKeyResolver {
    }

    interface Creator extends Function<BlackKeyResolver, BlackKeyResolver> {
    }

    private final List<Creator> creators;

    public static CompositeBlackKeyResolverBuilder builder() {
        return new CompositeBlackKeyResolverBuilder();
    }

    public CompositeBlackKeyResolverBuilder withIp() {
        this.creators.add((blackKeyResolver -> new CompositeBlackKeyResolver(blackKeyResolver, new IPKeyResolver())));
        return this;
    }

    public CompositeBlackKeyResolverBuilder withToken() {
        this.creators.add(blackKeyResolver -> new CompositeBlackKeyResolver(blackKeyResolver, new TokenBlackKeyResolver()));
        return this;
    }

    public BlackKeyResolver build() {
        BlackKeyResolver blackKeyResolver = new NoopBlackKeyResolver();
        for (Creator creator : creators) {
            blackKeyResolver = creator.apply(blackKeyResolver);
        }
        return blackKeyResolver;
    }
}
