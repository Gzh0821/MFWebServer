package cn.monkey.socket.netty.tcp.http.ws;

import cn.monkey.socket.netty.SimpleChannelOutboundHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class DefaultBinaryWebsocketFrameChannelOutboundHandler extends SimpleChannelOutboundHandler<byte[]> {
    @Override
    protected void write0(ChannelHandlerContext ctx, byte[] msg, ChannelPromise promise) {
        ctx.write(new BinaryWebSocketFrame(Unpooled.copiedBuffer(msg)), promise);
    }
}
