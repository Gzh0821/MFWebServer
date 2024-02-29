package cn.monkey.gateway.stream.ratelimit.async;

import cn.monkey.gateway.stream.ratelimit.config.RouteRateLimiterDefinition;
import reactor.core.publisher.Mono;

public interface ReactiveRateLimiterFactory {
    Mono<ReactiveRateLimiter> create(String id, RouteRateLimiterDefinition routeRateLimiterDefinition);
}
