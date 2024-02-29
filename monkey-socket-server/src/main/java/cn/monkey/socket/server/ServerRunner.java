package cn.monkey.socket.server;

import cn.monkey.socket.*;
import cn.monkey.socket.netty.DefaultFilterChain;
import cn.monkey.socket.netty.NettySessionManager;
import cn.monkey.socket.netty.tcp.http.ws.*;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ServerRunner implements ApplicationRunner, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final SocketServerProperties socketServerProperties;

    public ServerRunner(SocketServerProperties socketServerProperties) {
        this.socketServerProperties = socketServerProperties;
    }


    protected ChannelInboundHandler channelInboundHandler() {
        Map<String, BinaryFilter> filters = this.applicationContext.getBeansOfType(BinaryFilter.class);
        NettySessionManager sessionManager = this.applicationContext.getBean(NettySessionManager.class);
        BinaryDispatcher dispatcher = this.applicationContext.getBean(BinaryDispatcher.class);
        List<BinaryFilter> filterList = filters.values().stream().sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
        FilterChain<byte[]> filterChain = new DefaultFilterChain<>(new ArrayList<>(filterList));
        return new DefaultBinaryWebsocketChannelInboundHandler(sessionManager, filterChain, dispatcher);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        WebsocketServer.create()
                .channelClass(NioServerSocketChannel.class)
                .port(socketServerProperties.getPort())
                .protocol(socketServerProperties.getProtocol())
                .eventLoopGroup(new NioEventLoopGroup(socketServerProperties.getBossSize()))
                .childGroup(new NioEventLoopGroup(socketServerProperties.getWorkerSize()))
                .doOnChannelInit(channel -> channel.pipeline().addLast(new DefaultBinaryWebsocketFrameChannelOutboundHandler()))
                .doOnChannelInit(channel -> channel.pipeline().addLast(new DefaultTextWebSocketFrameChannelOutboundHandler()))
                .doOnChannelInit(channel -> channel.pipeline().addLast(channelInboundHandler()))
                .start();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
