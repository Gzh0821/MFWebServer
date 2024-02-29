package cn.monkey.socket.netty;

import cn.monkey.socket.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ServerTransport<S extends ServerTransport<S>> extends Transport<S, ServerTransportConfig> implements Server {

    private static final Logger log = LoggerFactory.getLogger(ServerTransport.class);

    private final ServerTransportConfig serverTransportConfig;
    private final Object monitor = new Object();
    private boolean isStarted = false;

    public ServerTransport() {
        this.serverTransportConfig = new ServerTransportConfig();
        this.configuration().mutate().shutDown(serverTransport -> {
            EventLoopGroup eventLoopGroup = serverTransport.configuration().eventLoopGroup();
            EventLoopGroup childGroup = serverTransport.configuration().childGroup();
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully();
            }
            if (childGroup != null) {
                childGroup.shutdownGracefully();
            }
        });
    }

    public S childGroup(EventLoopGroup childGroup) {
        configuration().mutate().childGroup(childGroup);
        return duplicate();
    }

    public S ssl(Supplier<SslContext> sslContextSupplier) {
        configuration().mutate().ssl(sslContextSupplier);
        return duplicate();
    }

    public S ssl(SslContext sslContext) {
        return ssl(() -> sslContext);
    }

    public S port(int port) {
        configuration().mutate().socketAddress(() -> new InetSocketAddress(port));
        return duplicate();
    }

    public S doOnChannelInit(Consumer<Channel> channelCustomizer) {
        configuration().mutate().doOnChannelInit(channelCustomizer);
        return duplicate();
    }

    public S channelClass(Class<? extends ServerSocketChannel> channelClass) {
        configuration().mutate().channelClass(channelClass);
        return duplicate();
    }

    public S bootStrapCustomizer(ServerBootstrapCustomizer serverBootstrapCustomizer) {
        configuration().mutate().bootstrapCustomizer(serverBootstrapCustomizer);
        return duplicate();
    }

    @Override
    public final void start() throws Exception {
        synchronized (this.monitor) {
            if (this.isStarted) {
                return;
            }
            this.start0();
            this.isStarted = true;
        }
    }

    protected void start0() throws Exception{
        ServerBootstrap server = this.createServer();
        try {
            log.info("server start address: {}, protocol: {}", configuration().socketAddress().get(), configuration());
            server.bind()
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            stop();
        }
    }

    protected void shutDown() {
        ServerTransportConfig configuration = configuration();
        ShutDown shutDown = configuration.shutDown();
        if (shutDown != null) {
            shutDown.accept(this);
        }
    }

    protected ServerBootstrap bindChannelOptions(ServerBootstrap serverBootstrap) {
        Map<ChannelOption<?>, ?> channelOptionMap = configuration().channelOptions();
        if (!CollectionUtils.isEmpty(channelOptionMap)) {
            for (Map.Entry<ChannelOption<?>, ?> e : channelOptionMap.entrySet()) {
                @SuppressWarnings("unchecked") ChannelOption<Object> key = (ChannelOption<Object>) e.getKey();
                Object value = e.getValue();
                serverBootstrap = serverBootstrap.childOption(key, value);
            }
        }
        return serverBootstrap;
    }

    protected void bindChannelAttributes(Channel channel) {
        Map<AttributeKey<?>, ?> channelAttributes = configuration().channelAttributes();
        if (!CollectionUtils.isEmpty(channelAttributes)) {
            for (Map.Entry<AttributeKey<?>, ?> e : channelAttributes.entrySet()) {
                @SuppressWarnings("unchecked") AttributeKey<Object> key = (AttributeKey<Object>) e.getKey();
                channel.attr(key).set(e.getValue());
            }
        }
    }

    protected ServerBootstrap createServer() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ServerTransportConfig configuration = configuration();
        SslContext ssl = configuration.ssl();
        final Consumer<Channel> channelCustomizer = configuration.channelCustomizer();
        serverBootstrap = bindChannelOptions(serverBootstrap
                .handler(configuration.loggingHandler()))
                .group(configuration.eventLoopGroup(), configuration.childGroup())
                .channel(configuration.channel())
                .handler(configuration.loggingHandler());
        if (ssl != null) {
            SSLEngine sslEngine = ssl.newEngine(ByteBufAllocator.DEFAULT);
            sslEngine.setUseClientMode(false);
            serverBootstrap.handler(new SslHandler(sslEngine));
        }
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(@NonNull SocketChannel ch) {
                    bindChannelAttributes(ch);
                    channelCustomizer.accept(ch);
                }
            });
        Function<ServerBootstrap, ServerBootstrap> serverBootstrapCustomizer = this.configuration().serverBootstrapCustomizer();
        if (serverBootstrapCustomizer != null) {
            serverBootstrap = serverBootstrapCustomizer.apply(serverBootstrap);
        }
        return serverBootstrap.localAddress(configuration().socketAddress().get());
    }

    @Override
    public ServerTransportConfig configuration() {
        return this.serverTransportConfig;
    }

}
