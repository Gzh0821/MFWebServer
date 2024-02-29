package cn.monkey.gateway.stream.blacklist.sync.local;

import cn.monkey.gateway.stream.blacklist.sync.FailCounter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.lang.NonNull;

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
            @NonNull
            public AtomicLong load(@NonNull String key) {
                return new AtomicLong(0L);
            }
        });
    }

    @Override
    public long getCount(String key) {
        return this.cache.getUnchecked(key).get();
    }

    @Override
    public long incrementAndGet(String key, long delta) {
        return this.cache.getUnchecked(key).addAndGet(delta);
    }
}
