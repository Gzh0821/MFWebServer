package cn.monkey.gateway.stream.ratelimit.async;

import cn.monkey.gateway.stream.ratelimit.config.RouteRateLimiterDefinition;
import reactor.core.publisher.Mono;


public interface ReactiveRateLimiterContainer {
    Mono<ReactiveRateLimiter> findOrCreate(String id, RouteRateLimiterDefinition definition);
}
