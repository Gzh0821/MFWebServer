package cn.monkey.socket.util;

import io.netty.channel.Channel;

public interface NettyChannelUtil {
    static String getId(Channel channel){
        return channel.id().asLongText();
    }
}
