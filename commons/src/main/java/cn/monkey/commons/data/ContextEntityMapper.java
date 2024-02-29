package cn.monkey.commons.data;

public interface ContextEntityMapper<D, T, V> extends EntityMapper<D, T, V> {
    EntityMapperContext getContext(String key);

    void remove(String key);
}
