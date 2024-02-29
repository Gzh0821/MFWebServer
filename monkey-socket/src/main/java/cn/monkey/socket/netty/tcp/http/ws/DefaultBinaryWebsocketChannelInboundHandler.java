package cn.monkey.socket.netty.tcp.http.ws;

import cn.monkey.socket.Dispatcher;
import cn.monkey.socket.FilterChain;
import cn.monkey.socket.netty.NettySessionManager;
import cn.monkey.socket.netty.SessionChannelInboundHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.springframework.lang.Nullable;

@ChannelHandler.Sharable
public class DefaultBinaryWebsocketChannelInboundHandler extends SessionChannelInboundHandler<BinaryWebSocketFrame,byte[]> {
    public DefaultBinaryWebsocketChannelInboundHandler(NettySessionManager nettySessionManager,
                                                       FilterChain<byte[]> filterChain,
                                                       @Nullable Dispatcher<byte[]> dispatcher) {
        super(nettySessionManager, filterChain, dispatcher);
    }

    @Override
    protected byte[] decode(BinaryWebSocketFrame msg) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg.content());
        try {
            return byteBuf.array();
        }finally {
            ReferenceCountUtil.release(byteBuf);
        }
    }
}
