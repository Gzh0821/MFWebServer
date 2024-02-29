package cn.monkey.gateway.stream.blacklist.async.redisson;

import cn.monkey.gateway.stream.blacklist.async.FailCounter;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedissonFailCounter implements FailCounter {

    private final RedissonClient redissonClient;
    private final long maxCountIntervalMs;

    private final String prefix;

    public RedissonFailCounter(String prefix, RedissonClient redissonClient, long maxCountIntervalMs) {
        this.prefix = prefix;
        this.redissonClient = redissonClient;
        this.maxCountIntervalMs = maxCountIntervalMs;
    }

    @Override
    public Mono<Long> getCount(String key) {
        key = this.prefix + key;
        RAtomicLong counter = this.redissonClient.getAtomicLong(key);
        return Mono.fromCompletionStage(counter.getAsync());
    }

    @Override
    public Mono<Long> incrementAndGet(String key, long delta) {
        key = this.prefix + key;
        RAtomicLong counter = this.redissonClient.getAtomicLong(key);
        return Mono.defer(() -> Mono.fromCompletionStage(counter.expireAsync(Duration.of(this.maxCountIntervalMs, TimeUnit.MILLISECONDS.toChronoUnit()))))
                .flatMap(a -> Mono.fromCompletionStage(counter.addAndGetAsync(delta)));
    }
}
