package cn.monkey.commons.data;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Data
public abstract class Range<T extends Serializable> implements Serializable {
    private T start;
    private T end;

    protected static <T extends Serializable, R extends Range<T>> R of(T start, T end, Class<R> clazz) {
        try {
            Constructor<R> constructor = clazz.getConstructor();
            R r = constructor.newInstance();
            r.setStart(start);
            r.setEnd(end);
            return r;
        } catch (NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}