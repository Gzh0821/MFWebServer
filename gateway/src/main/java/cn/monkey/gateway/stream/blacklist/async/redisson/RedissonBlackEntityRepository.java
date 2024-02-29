package cn.monkey.gateway.stream.blacklist.async.redisson;

import cn.monkey.gateway.stream.blacklist.BlackEntity;
import cn.monkey.gateway.stream.blacklist.async.BlackEntityRepository;
import org.redisson.api.RFuture;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public class RedissonBlackEntityRepository implements BlackEntityRepository {

    private final RMapCache<String, BlackEntity> mapCache;

    private final long autoRemoveTimeMs;

    public RedissonBlackEntityRepository(String name, RedissonClient redissonClient, long autoRemoveTimeMs) {
        this.mapCache = redissonClient.getMapCache(name);
        if (!mapCache.isExists()) {
            mapCache.trySetMaxSize(Integer.MAX_VALUE);
        }
        this.autoRemoveTimeMs = autoRemoveTimeMs;
    }

    @Override
    public Mono<Boolean> containsKey(String key) {
        return Mono.fromCompletionStage(this.mapCache.containsKeyAsync(key).toCompletableFuture());
    }

    @Override
    public Mono<BlackEntity> add(BlackEntity blackEntity) {
        RFuture<BlackEntity> future;
        if (this.autoRemoveTimeMs > 0) {
            future = this.mapCache.putAsync(blackEntity.key(), blackEntity, this.autoRemoveTimeMs, TimeUnit.MILLISECONDS);
        } else {
            future = this.mapCache.putAsync(blackEntity.key(), blackEntity);
        }
        return Mono.fromCompletionStage(future.toCompletableFuture());
    }

    @Override
    public Mono<BlackEntity> remove(BlackEntity blackEntity) {
        return Mono.fromCompletionStage(this.mapCache.removeAsync(blackEntity.key()));
    }
}
