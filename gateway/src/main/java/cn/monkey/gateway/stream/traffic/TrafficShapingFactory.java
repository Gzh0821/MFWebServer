package cn.monkey.gateway.stream.traffic;

import io.netty.channel.ChannelHandler;
import org.springframework.beans.factory.FactoryBean;

import java.util.concurrent.ScheduledExecutorService;

public interface TrafficShapingFactory extends FactoryBean<ChannelHandler> {
    @Override
    default Class<?> getObjectType() {
        return ChannelHandler.class;
    }

    void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService);
}
