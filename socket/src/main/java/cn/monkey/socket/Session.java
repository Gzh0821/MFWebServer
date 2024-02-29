package cn.monkey.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;

public interface Session extends Closeable {

    String id();

    <T> T setAttribute(String key, T val);

    <T> T get(String key);

    void write(Object data);

    boolean isActive();

    @Override
    void close() throws IOException;

    SocketAddress getRemoteAddress();

    void bind();
}
