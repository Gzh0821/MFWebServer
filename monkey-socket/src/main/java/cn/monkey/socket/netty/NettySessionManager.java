package cn.monkey.socket.netty;

import cn.monkey.socket.SessionManager;
import io.netty.channel.ChannelHandlerContext;

public interface NettySessionManager extends SessionManager<ChannelHandlerContext> {
    @Override
    NettySession findOrCreate(ChannelHandlerContext channelHandlerContext);
}
