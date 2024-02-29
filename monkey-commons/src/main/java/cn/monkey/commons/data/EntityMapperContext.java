package cn.monkey.commons.data;

public interface EntityMapperContext {
    <T> T get(String key, Class<T> type);

    void put(String key, Object v);
}
