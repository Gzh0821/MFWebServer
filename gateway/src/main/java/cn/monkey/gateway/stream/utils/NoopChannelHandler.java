package cn.monkey.gateway.stream.utils;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class NoopChannelHandler extends ChannelDuplexHandler {
}
