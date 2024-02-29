package cn.monkey.socket.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class SimpleChannelOutboundHandler<T> extends ChannelOutboundHandlerAdapter {

    private final TypeParameterMatcher matcher;

    public SimpleChannelOutboundHandler() {
        this.matcher = TypeParameterMatcher.find(this, SimpleChannelOutboundHandler.class, "T");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg == null || !this.matcher.match(msg)) {
            super.write(ctx, msg, promise);
            return;
        }
        this.write0(ctx, (T) msg, promise);
    }

    protected abstract void write0(ChannelHandlerContext ctx, T msg, ChannelPromise promise);
}
