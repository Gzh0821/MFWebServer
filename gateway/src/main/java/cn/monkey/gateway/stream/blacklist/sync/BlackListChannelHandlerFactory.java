package cn.monkey.gateway.stream.blacklist.sync;

import io.netty.channel.ChannelHandler;
import org.springframework.beans.factory.FactoryBean;

public interface BlackListChannelHandlerFactory extends FactoryBean<ChannelHandler> {
    @Override
    default Class<?> getObjectType() {
        return BlackListChannelHandler.class;
    }
}
