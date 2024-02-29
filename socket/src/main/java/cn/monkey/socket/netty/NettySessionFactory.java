package cn.monkey.socket.netty;

import cn.monkey.socket.SessionCreateException;
import cn.monkey.socket.SessionFactory;
import io.netty.channel.ChannelHandlerContext;

public interface NettySessionFactory extends SessionFactory<ChannelHandlerContext> {
    @Override
    NettySession apply(ChannelHandlerContext channelHandlerContext) throws SessionCreateException;
}
