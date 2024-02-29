package cn.monkey.socket.netty.tcp.http.ws;

import cn.monkey.socket.netty.tcp.TcpServer;
import cn.monkey.socket.util.ChannelHandlerUtils;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;

public class WebsocketServer extends TcpServer {
    private WebsocketServer() {
        this.channelOption(ChannelOption.SO_KEEPALIVE, true)
                .configuration()
                .mutate()
                .eventLoopGroup(new NioEventLoopGroup())
                .childGroup(new NioEventLoopGroup())
                .socketAddress(() -> new InetSocketAddress(8080))
                .loggingHandler(new LoggingHandler())
                .doOnChannelInit(channel -> {
                    ChannelPipeline pipeline = channel.pipeline();
                    // @see https://www.cnblogs.com/UncleCatMySelf/p/9190637.html
                    pipeline.addLast(ChannelHandlerUtils.normalizeName(HttpServerCodec.class), new HttpServerCodec())
                            .addLast(ChannelHandlerUtils.normalizeName(ChunkedWriteHandler.class), new ChunkedWriteHandler())
                            .addLast(ChannelHandlerUtils.normalizeName(HttpObjectAggregator.class), new HttpObjectAggregator(60 * 1024))
                            .addLast(ChannelHandlerUtils.normalizeName(DefaultTextWebSocketFrameChannelOutboundHandler.class), new DefaultTextWebSocketFrameChannelOutboundHandler())
                            .addLast(ChannelHandlerUtils.normalizeName(DefaultBinaryWebsocketFrameChannelOutboundHandler.class), new DefaultBinaryWebsocketFrameChannelOutboundHandler());
                });
    }

    public static WebsocketServer create() {
        return new WebsocketServer();
    }

    public WebsocketServer protocol(String protocol) {
        this.configuration().mutate().doOnChannelInit(channel ->
                channel.pipeline()
                        .addLast(new WebSocketServerProtocolHandler(protocol)));
        return (WebsocketServer) duplicate();
    }


    @Override
    public void stop() {
        shutDown();
    }
}
