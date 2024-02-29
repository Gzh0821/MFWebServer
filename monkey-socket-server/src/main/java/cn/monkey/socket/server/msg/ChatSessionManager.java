package cn.monkey.socket.server.msg;

import cn.monkey.socket.Session;
import cn.monkey.socket.netty.DefaultNettySessionManager;
import cn.monkey.socket.netty.NettySession;
import cn.monkey.socket.netty.NettySessionFactory;
import cn.monkey.socket.server.msg.data.User;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class ChatSessionManager extends DefaultNettySessionManager implements UserSessionRepository {

    private volatile Map<String, List<Session>> userSessionMap;

    public ChatSessionManager(NettySessionFactory sessionFactory) {
        super(sessionFactory);
        this.userSessionMap = new ConcurrentHashMap<>();
    }

    @Override
    public NettySession findOrCreate(ChannelHandlerContext channelHandlerContext) {
        return super.findOrCreate(channelHandlerContext);
    }

    @Override
    public void refresh() {
        Map<String, NettySession> sessionMap = this.sessionMap;
        Map<String, NettySession> copySessionMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, NettySession> e : sessionMap.entrySet()) {
            NettySession value = e.getValue();
            if (!value.isActive() || value.get(User.KEY) == null) {
                try {
                    value.close();
                } catch (IOException ignore) {
                }
                continue;
            }
            copySessionMap.put(e.getKey(), value);
        }
        if (SESSION_MAP_HANDLE.compareAndSet(this, copySessionMap, sessionMap)) {
            this.userSessionMap = copySessionMap.values()
                    .stream().collect(Collectors.groupingBy(session -> ((User) session.get(User.KEY)).getId()));
        }
    }

    @Override
    public List<Session> selectByUid(String uid) {
        return this.userSessionMap.get(uid);
    }
}
