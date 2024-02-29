package cn.monkey.gateway.stream.blacklist.sync.local;

import cn.monkey.gateway.stream.blacklist.BlackEntity;
import cn.monkey.gateway.stream.blacklist.sync.BlackEntityRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InMemoryBlackEntityRepository implements BlackEntityRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryBlackEntityRepository.class);
    private final Cache<String, BlackEntity> cache;

    public InMemoryBlackEntityRepository(long autoRemoveTimeMs) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().maximumSize(Short.MAX_VALUE);
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

    public InMemoryBlackEntityRepository() {
        this(0L);
    }

    @Override
    public List<BlackEntity> selectAll() {
        Collection<BlackEntity> values = cache.asMap().values();
        return ImmutableList.copyOf(values);
    }

    @Override
    public boolean containsKey(String key) {
        return this.cache.asMap().containsKey(key);
    }

    @Override
    public BlackEntity add(BlackEntity blackListEntity) {
        return this.cache.asMap().put(blackListEntity.key(), blackListEntity);
    }

    @Override
    public BlackEntity remove(BlackEntity blackListEntity) {
        return this.cache.asMap().remove(blackListEntity.key());
    }
}
