package cn.monkey.state.core;

public interface StateContext {

    StateContext EMPTY = new StateContext() {
    };

    default boolean autoUpdate() {
        return false;
    }

    default <T> T get(String key, Class<T> c) {
        throw new UnsupportedOperationException();
    }

    default void put(String key, Object v) {
        throw new UnsupportedOperationException();
    }
}
