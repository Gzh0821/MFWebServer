package cn.monkey.commons.data;

public interface Identifiable<T> {
    T getId();

    void setId(T id);
}
