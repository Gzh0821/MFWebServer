package cn.monkey.gateway.stream.ratelimit.async.redisson;

import cn.monkey.gateway.stream.ratelimit.async.ReactiveRateLimiter;
import cn.monkey.gateway.stream.ratelimit.async.ReactiveRateLimiterFactory;
import cn.monkey.gateway.stream.ratelimit.config.RouteRateLimiterDefinition;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedissonReactiveRateLimiterFactory implements ReactiveRateLimiterFactory {
    private final RedissonClient redissonClient;

    public RedissonReactiveRateLimiterFactory(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Mono<ReactiveRateLimiter> create(String id, RouteRateLimiterDefinition routeRateLimiterDefinition) {
        RRateLimiter rateLimiter = this.redissonClient.getRateLimiter(id);
        Mono<Void> voidMono = Mono.fromCompletionStage(rateLimiter.isExistsAsync().toCompletableFuture())
                .flatMap(isExists -> {
                    if (!isExists) {
                        return Mono.fromCompletionStage(rateLimiter.setRateAsync(RateType.OVERALL,
                                routeRateLimiterDefinition.getWarmupPeriodMs(),
                                (long) (routeRateLimiterDefinition.getPermitsPerSecond() * 1000),
                                RateIntervalUnit.MILLISECONDS));
                    }
                    return Mono.empty();
                });
        long expireTimeMs = routeRateLimiterDefinition.getExpireTimeMs();
        if (expireTimeMs > 0) {
            voidMono = voidMono.then(Mono.fromCompletionStage(rateLimiter.expireIfGreaterAsync(Duration.of(expireTimeMs, TimeUnit.MILLISECONDS.toChronoUnit())))).then();
        }
        return voidMono.then(Mono.just(() -> Mono.fromCompletionStage(rateLimiter.tryAcquireAsync().toCompletableFuture())));
    }
}
