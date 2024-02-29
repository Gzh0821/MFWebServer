package cn.monkey.socket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.handler.ssl.SslContext;

import java.util.function.Function;
import java.util.function.Supplier;

public class ServerTransportConfig extends TransportConfig<ServerTransportConfig, ServerTransportConfig.Builder, ServerSocketChannel> {


    ServerTransportConfig() {
        super();
    }

    EventLoopGroup childGroup = new DefaultEventLoopGroup();

    Supplier<SslContext> sslContextSupplier;

    Function<ServerBootstrap, ServerBootstrap> serverBootstrapCustomizer;

    @Override
    public Builder mutate() {
        return new Builder(duplicate());
    }

    public EventLoopGroup childGroup() {
        return this.childGroup;
    }

    public SslContext ssl() {
        return this.sslContextSupplier == null ? null : sslContextSupplier.get();
    }

    public Function<ServerBootstrap, ServerBootstrap> serverBootstrapCustomizer(){
        return this.serverBootstrapCustomizer;
    }

    public class Builder extends TransportConfig<ServerTransportConfig, Builder, ServerSocketChannel>.Builder {

        Builder(ServerTransportConfig serverTransportConfig) {
            super(serverTransportConfig);
        }

        public Builder childGroup(EventLoopGroup childGroup) {
            build().childGroup = childGroup;
            return duplicate();
        }

        public Builder bootstrapCustomizer(ServerBootstrapCustomizer serverBootstrapCustomizer) {
            ServerTransportConfig config = build();
            if (config.serverBootstrapCustomizer == null) {
                config.serverBootstrapCustomizer = serverBootstrapCustomizer;
            }
            config.serverBootstrapCustomizer = config.serverBootstrapCustomizer
                    .andThen(serverBootstrapCustomizer);
            return duplicate();
        }

        public Builder ssl(Supplier<SslContext> sslContextSupplier) {
            build().sslContextSupplier = sslContextSupplier;
            return duplicate();
        }
    }
}
