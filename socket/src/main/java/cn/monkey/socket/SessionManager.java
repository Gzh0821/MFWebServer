package cn.monkey.socket;

public interface SessionManager<T> {
    Session findOrCreate(T t);
}
