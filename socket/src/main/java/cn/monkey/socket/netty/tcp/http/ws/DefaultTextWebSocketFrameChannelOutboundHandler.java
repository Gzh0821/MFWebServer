package cn.monkey.socket.netty.tcp.http.ws;

import cn.monkey.socket.netty.SimpleChannelOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class DefaultTextWebSocketFrameChannelOutboundHandler extends SimpleChannelOutboundHandler<String> {
    @Override
    protected void write0(ChannelHandlerContext ctx, String msg, ChannelPromise promise) {
        ctx.write(new TextWebSocketFrame(msg));
    }
}
