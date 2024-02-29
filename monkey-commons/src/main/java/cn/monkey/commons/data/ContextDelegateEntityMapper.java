package cn.monkey.commons.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextDelegateEntityMapper<D, T, V> implements ContextEntityMapper<D, T, V> {
    private final EntityMapper<D, T, V> delegate;

    protected final Map<String, EntityMapperContext> entityMapperContextMap;

    public ContextDelegateEntityMapper(EntityMapper<D, T, V> delegate) {
        this.delegate = delegate;
        entityMapperContextMap = new ConcurrentHashMap<>();
    }

    @Override
    public EntityMapperContext getContext(String key) {
        return this.entityMapperContextMap.get(key);
    }

    @Override
    public void remove(String key) {
        this.entityMapperContextMap.remove(key);
    }

    @Override
    public T copyFromDto(D d) {
        return this.delegate.copyFromDto(d);
    }

    @Override
    public V copyToVo(T t) {
        return this.delegate.copyToVo(t);
    }
}
