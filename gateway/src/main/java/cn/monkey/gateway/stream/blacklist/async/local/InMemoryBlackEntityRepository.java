package cn.monkey.gateway.stream.blacklist.async.local;

import cn.monkey.gateway.stream.blacklist.BlackEntity;
import cn.monkey.gateway.stream.blacklist.async.BlackEntityRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public class InMemoryBlackEntityRepository implements BlackEntityRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryBlackEntityRepository.class);
    private final Cache<String, BlackEntity> cache;

    public InMemoryBlackEntityRepository(long autoRemoveTimeMs) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if (autoRemoveTimeMs > 0) {
            builder.expireAfterWrite(autoRemoveTimeMs, TimeUnit.MILLISECONDS);
        }
        this.cache = builder.removalListener(notification -> {
            if (log.isDebugEnabled()) {
                Object key = notification.getKey();
                log.debug("remove black entity, id: {}, entity: {}", key, notification.getValue());
            }
        }).build();
    }
    @Override
    public Mono<Boolean> containsKey(String key) {
        return Mono.just(this.cache.asMap().containsKey(key));
    }

    @Override
    public Mono<BlackEntity> add(BlackEntity blackEntity) {
        return Mono.justOrEmpty(this.cache.asMap().put(blackEntity.key(), blackEntity));
    }

    @Override
    public Mono<BlackEntity> remove(BlackEntity blackEntity) {
        return Mono.just(this.cache.asMap().remove(blackEntity.key()));
    }
}
