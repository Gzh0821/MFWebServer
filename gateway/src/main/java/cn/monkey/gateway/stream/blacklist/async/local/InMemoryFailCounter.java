package cn.monkey.gateway.stream.blacklist.async.local;

import cn.monkey.gateway.stream.blacklist.async.FailCounter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryFailCounter implements FailCounter {

    private final LoadingCache<String, AtomicLong> cache;

    public InMemoryFailCounter(long maxCountIntervalMs) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if (maxCountIntervalMs > 0) {
            builder.expireAfterAccess(maxCountIntervalMs, TimeUnit.MILLISECONDS);
        }
        this.cache = builder.build(new CacheLoader<>() {
            @Override
            public AtomicLong load(String key) {
                return new AtomicLong(0L);
            }
        });
    }

    @Override
    public Mono<Long> getCount(String key) {
        return Mono.just(this.cache.getUnchecked(key).get());
    }

    @Override
    public Mono<Long> incrementAndGet(String key, long delta) {
        return Mono.just(this.cache.getUnchecked(key).addAndGet(delta));
    }

}