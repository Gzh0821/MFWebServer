package cn.monkey.orm;

public interface BeforeUpdateBehavior<T> {
    default void beforeUpdate(T t) {
    }
}
