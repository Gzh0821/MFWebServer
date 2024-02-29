package cn.monkey.socket.netty;

import cn.monkey.commons.bean.Refreshable;
import cn.monkey.socket.util.NettyChannelUtil;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultNettySessionManager implements NettySessionManager, Refreshable {

    protected volatile Map<String, NettySession> sessionMap;

    protected final NettySessionFactory sessionFactory;

    protected static final VarHandle SESSION_MAP_HANDLE;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            SESSION_MAP_HANDLE = lookup.findVarHandle(DefaultNettySessionManager.class, "sessionMap", Map.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultNettySessionManager(NettySessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.sessionMap = new ConcurrentHashMap<>();
    }

    @Override
    public NettySession findOrCreate(ChannelHandlerContext channelHandlerContext) {
        final Map<String, NettySession> sessionMap = this.sessionMap;
        NettySession session = sessionMap.computeIfAbsent(NettyChannelUtil.getId(channelHandlerContext.channel()), (key) -> this.sessionFactory.apply(channelHandlerContext));
        if (session instanceof HeartBeatSession heartBeatSession) {
            heartBeatSession.flushLastOperateTime();
        }
        this.sessionMap = sessionMap;
        return session;
    }

    @Override
    public void refresh() {
        Map<String, NettySession> sessionMap = this.sessionMap;
        Map<String, NettySession> copySessionMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, NettySession> e : sessionMap.entrySet()) {
            NettySession value = e.getValue();
            if (value.isActive()) {
                copySessionMap.put(e.getKey(), value);
                continue;
            }
            try {
                value.close();
            } catch (IOException ignore) {
            }
        }
        SESSION_MAP_HANDLE.compareAndSet(this, copySessionMap, sessionMap);
    }
}
