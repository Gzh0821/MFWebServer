package cn.monkey.gateway.stream.ratelimit.async;

import cn.monkey.gateway.stream.ratelimit.RateLimiter;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveRateLimiter extends RateLimiter {
    Mono<Boolean> tryAcquire();
}
