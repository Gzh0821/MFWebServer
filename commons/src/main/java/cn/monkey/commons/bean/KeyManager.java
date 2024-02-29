package cn.monkey.commons.bean;

public interface KeyManager<T> {
    String encrypt(T t);

    T decrypt(String s);
}
