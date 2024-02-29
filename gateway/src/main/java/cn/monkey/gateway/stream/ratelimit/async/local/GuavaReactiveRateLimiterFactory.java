package cn.monkey.gateway.stream.ratelimit.async.local;

import cn.monkey.gateway.stream.ratelimit.async.ReactiveRateLimiter;
import cn.monkey.gateway.stream.ratelimit.async.ReactiveRateLimiterFactory;
import cn.monkey.gateway.stream.ratelimit.config.RouteRateLimiterDefinition;
import com.google.common.util.concurrent.RateLimiter;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public class GuavaReactiveRateLimiterFactory implements ReactiveRateLimiterFactory {
    @Override
    public Mono<ReactiveRateLimiter> create(String id, RouteRateLimiterDefinition routeRateLimiterDefinition) {
        RateLimiter rateLimiter = RateLimiter.create(routeRateLimiterDefinition.getPermitsPerSecond(), routeRateLimiterDefinition.getWarmupPeriodMs(), TimeUnit.MILLISECONDS);
        return Mono.just(() -> Mono.just(rateLimiter.tryAcquire()));
    }
}
