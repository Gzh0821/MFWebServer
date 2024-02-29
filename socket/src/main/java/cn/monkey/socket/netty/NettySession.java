package cn.monkey.socket.netty;

import cn.monkey.socket.Session;
import cn.monkey.socket.util.NettyChannelUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettySession implements Session {

    public static final AttributeKey<NettySession> SESSION_KEY  = AttributeKey.newInstance("session");

    private static final Map<String, AttributeKey<?>> ATTRIBUTE_KEY_MAP = new ConcurrentHashMap<>();
    private final String id;

    protected final Channel channel;

    public NettySession(Channel channel) {
        this.channel = channel;
        this.id = NettyChannelUtil.getId(channel);
    }

    public NettySession(NettySession replaced) {
        this.channel = replaced.channel;
        this.id = replaced.id;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T setAttribute(String key, T val) {
        AttributeKey<T> attributeKey = (AttributeKey<T>) ATTRIBUTE_KEY_MAP.computeIfAbsent(key, AttributeKey::newInstance);
        return channel.attr(attributeKey).getAndSet(val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        AttributeKey<?> attributeKey = ATTRIBUTE_KEY_MAP.get(key);
        if (attributeKey == null) {
            throw new NullPointerException("invalid key: " + key);
        }
        return (T) channel.attr(attributeKey).get();
    }

    @Override
    public void write(Object obj) {
        this.channel.writeAndFlush(obj);
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return this.channel.remoteAddress();
    }

    @Override
    public void bind() {
        this.channel.attr(SESSION_KEY).set(this);
    }
}
