package cn.monkey.gateway.stream.ratelimit.async;

import cn.monkey.gateway.stream.ratelimit.config.RouteRateLimiterDefinition;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import reactor.core.publisher.Mono;


public class DefaultReactiveRateLimiterContainer implements ReactiveRateLimiterContainer {

    private final ReactiveRateLimiterFactory reactiveRateLimiterFactory;

    private final CaffeineCacheManager cacheManager;

    public DefaultReactiveRateLimiterContainer(ReactiveRateLimiterFactory reactiveRateLimiterFactory) {
        this.reactiveRateLimiterFactory = reactiveRateLimiterFactory;
        this.cacheManager = new CaffeineCacheManager();
    }

    protected Cache getCache(String routeId) {
        Cache cache = cacheManager.getCache(routeId);
        if (cache == null) {
            com.github.benmanes.caffeine.cache.Cache<Object, Object> build = Caffeine.newBuilder().build();
            cacheManager.registerCustomCache(routeId, build);
            cache = cacheManager.getCache(routeId);
        }
        return cache;
    }

    @Override
    public Mono<ReactiveRateLimiter> findOrCreate(String id, RouteRateLimiterDefinition definition) {
        String routeId = definition.getRouteId() == null ? "" : definition.getRouteId();
        Cache cache = this.getCache(routeId);
        return Mono.justOrEmpty(cache.get(id, ReactiveRateLimiter.class))
                .switchIfEmpty(this.reactiveRateLimiterFactory.create(id, definition))
                .doOnNext(iRateLimiter -> cache.put(id, iRateLimiter));
    }
}
