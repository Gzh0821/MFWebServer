package cn.monkey.socket.netty;

import cn.monkey.socket.SessionCreateException;
import io.netty.channel.ChannelHandlerContext;

public class DefaultNettySessionFactory implements NettySessionFactory {
    @Override
    public NettySession apply(ChannelHandlerContext channelHandlerContext) throws SessionCreateException {
        return new NettySession(channelHandlerContext.channel());
    }
}
