package cn.monkey.orm;

public interface BeforeCreateBehavior<T> {
    default void beforeCreate(T t) {
    }
}
