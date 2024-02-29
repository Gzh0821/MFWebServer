package cn.monkey.socket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ServerChannel;

public interface ServerBootstrapCustomizer extends BootstrapCustomizer<ServerBootstrap, ServerChannel> {

}
