package cn.monkey.test.WebsocketTest;

import cn.monkey.socket.Dispatcher;
import cn.monkey.socket.FilterChain;
import cn.monkey.socket.netty.*;
import cn.monkey.socket.netty.tcp.http.ws.DefaultTextWebSocketChannelInboundHandler;
import cn.monkey.socket.netty.tcp.http.ws.WebsocketServer;
import com.google.gson.Gson;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WebsocketTest {
    @Test
    public void test01() throws Exception {
        NettySessionFactory nettySessionFactory = new DefaultNettySessionFactory();
        NettySessionManager nettySessionManager = new DefaultNettySessionManager(nettySessionFactory);
        DefaultTextWebSocketChannelInboundHandler channelInboundHandler = getDefaultWebSocketChannelInboundHandler(nettySessionManager);
        WebsocketServer.create().protocol("/ws").doOnChannelInit(channel -> {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(channelInboundHandler);
        }).channelClass(NioServerSocketChannel.class).start();
    }

    private static DefaultTextWebSocketChannelInboundHandler getDefaultWebSocketChannelInboundHandler(NettySessionManager nettySessionManager) {
        FilterChain<String> filterChain = new DefaultFilterChain<>(Collections.emptyList());
        Dispatcher<String> dispatcher = (session, s) -> {
            System.out.println("s:" + s);
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("111", "2222");
            session.write(new Gson().toJson(jsonMap).getBytes());
        };

        return new DefaultTextWebSocketChannelInboundHandler(nettySessionManager, filterChain, dispatcher);
    }
}
