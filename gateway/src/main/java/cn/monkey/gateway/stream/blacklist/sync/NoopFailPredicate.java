package cn.monkey.gateway.stream.blacklist.sync;

import io.netty.channel.ChannelHandlerContext;

public class NoopFailPredicate implements FailPredicate {

    @Override
    public boolean test(ChannelHandlerContext ctx, Object msg) {
        return false;
    }
}
