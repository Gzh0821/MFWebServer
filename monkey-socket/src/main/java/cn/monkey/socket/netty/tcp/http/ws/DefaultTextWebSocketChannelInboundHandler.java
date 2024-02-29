package cn.monkey.socket.netty.tcp.http.ws;

import cn.monkey.socket.Dispatcher;
import cn.monkey.socket.FilterChain;
import cn.monkey.socket.netty.NettySessionManager;
import cn.monkey.socket.netty.SessionChannelInboundHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.springframework.lang.Nullable;

@ChannelHandler.Sharable
public class DefaultTextWebSocketChannelInboundHandler extends SessionChannelInboundHandler<TextWebSocketFrame, String> {

    public DefaultTextWebSocketChannelInboundHandler(NettySessionManager nettySessionManager, FilterChain<String> filterChain, @Nullable Dispatcher<String> dispatcher) {
        super(nettySessionManager, filterChain, dispatcher);
    }

    @Override
    protected String decode(TextWebSocketFrame webSocketFrame) {
        return webSocketFrame.text();
    }
}
