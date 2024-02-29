package cn.monkey.socket;

import java.util.function.Function;

public interface SessionFactory<T> extends Function<T, Session> {
    Session apply(T t) throws SessionCreateException;
}
