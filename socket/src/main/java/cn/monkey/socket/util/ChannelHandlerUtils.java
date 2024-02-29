package cn.monkey.socket.util;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerUtils {
    static String normalizeName(Class<? extends ChannelHandler> clazz) {
        return clazz.getSimpleName();
    }
}
