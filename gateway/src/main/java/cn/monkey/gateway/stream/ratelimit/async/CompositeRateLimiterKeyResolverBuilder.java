package cn.monkey.gateway.stream.ratelimit.async;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public final class CompositeRateLimiterKeyResolverBuilder {
    interface Creator extends Function<ReactiveKeyResolver, ReactiveKeyResolver> {
    }

    private CompositeRateLimiterKeyResolverBuilder() {
        this.creators = new ArrayList<>();
    }

    private final Collection<Creator> creators;

    public static CompositeRateLimiterKeyResolverBuilder builder() {
        return new CompositeRateLimiterKeyResolverBuilder();
    }

    public CompositeRateLimiterKeyResolverBuilder with(ReactiveKeyResolver reactiveKeyResolver) {
        this.creators.add((resolver) -> new CompositeRateLimiterKeyResolver(resolver, reactiveKeyResolver));
        return this;
    }

    public CompositeRateLimiterKeyResolverBuilder withIp() {
        return this.with(new IPKeyResolver());
    }

    public CompositeRateLimiterKeyResolverBuilder withToken() {
        return this.with(new TokenKeyResolver());
    }

    public ReactiveKeyResolver build() {
        ReactiveKeyResolver keyResolver = new NOOPKeyResolver();
        if (CollectionUtils.isEmpty(this.creators)) {
            return keyResolver;
        }
        for (Creator creator : this.creators) {
            keyResolver = creator.apply(keyResolver);
        }
        return keyResolver;
    }
}
