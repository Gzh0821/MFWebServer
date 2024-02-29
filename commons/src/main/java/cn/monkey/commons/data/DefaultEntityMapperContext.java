package cn.monkey.commons.data;

import java.util.HashMap;
import java.util.Map;

public class DefaultEntityMapperContext implements EntityMapperContext {
    protected final Map<String, Object> data;

    public DefaultEntityMapperContext() {
        this.data = new HashMap<>();
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Object o;
        return (o = data.get(key)) == null ? null : type.cast(o);
    }

    @Override
    public void put(String key, Object v) {
        this.data.put(key, v);
    }
}
