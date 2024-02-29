package cn.monkey.socket.netty;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;

import java.util.function.Function;

public interface BootstrapCustomizer<B extends AbstractBootstrap<B, C>, C extends Channel> extends Function<B, B> {

}
