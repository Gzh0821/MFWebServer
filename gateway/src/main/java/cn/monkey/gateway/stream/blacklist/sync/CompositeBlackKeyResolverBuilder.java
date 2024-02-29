package cn.monkey.gateway.stream.blacklist.sync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public final class CompositeBlackKeyResolverBuilder {
    private CompositeBlackKeyResolverBuilder() {
        this.creators = new ArrayList<>();
    }

    static class NoopBlackKeyResolver implements BlackKeyResolver {
    }

    interface Creator extends Function<BlackKeyResolver, BlackKeyResolver> {
    }

    private final Collection<Creator> creators;

    public static CompositeBlackKeyResolverBuilder builder() {
        return new CompositeBlackKeyResolverBuilder();
    }

    public CompositeBlackKeyResolverBuilder withIp() {
        this.creators.add((blackKeyResolver -> new CompositeBlackKeyResolver(blackKeyResolver, new IPKeyResolver())));
        return this;
    }

    public CompositeBlackKeyResolverBuilder withToken() {
        this.creators.add((blackKeyResolver -> new CompositeBlackKeyResolver(blackKeyResolver, new TokenKeyResolver())));
        return this;
    }

    public BlackKeyResolver build() {
        BlackKeyResolver blackKeyResolver = new NoopBlackKeyResolver();
        if (this.creators.size() == 0) {
            return blackKeyResolver;
        }
        for (Creator creator : this.creators) {
            blackKeyResolver = creator.apply(blackKeyResolver);
        }
        return blackKeyResolver;
    }
}
