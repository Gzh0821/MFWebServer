package cn.monkey.gateway.stream.blacklist.sync;

import io.netty.channel.ChannelHandlerContext;

public interface FailPredicate {
    boolean test(ChannelHandlerContext ctx, Object msg);
}
