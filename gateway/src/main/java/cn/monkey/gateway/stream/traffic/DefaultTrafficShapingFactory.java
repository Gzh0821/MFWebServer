package cn.monkey.gateway.stream.traffic;

import cn.monkey.gateway.stream.traffic.config.TrafficShapingConfigurationProperties;
import cn.monkey.gateway.stream.utils.NoopChannelHandler;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.netty.channel.ChannelHandler;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DefaultTrafficShapingFactory implements TrafficShapingFactory {

    private final TrafficShapingConfigurationProperties trafficShapingConfigurationProperties;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public DefaultTrafficShapingFactory(TrafficShapingConfigurationProperties trafficShapingConfigurationProperties) {
        this.trafficShapingConfigurationProperties = trafficShapingConfigurationProperties;
    }

    @Override
    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        Preconditions.checkArgument(scheduledExecutorService != null, "scheduledExecutorService can not be null");
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public ChannelHandler getObject() {
        if (!this.trafficShapingConfigurationProperties.isEnabled()) {
            return new NoopChannelHandler();
        }
        String className = trafficShapingConfigurationProperties.getClassName();
        if (Strings.isNullOrEmpty(className)) {
            throw new IllegalArgumentException("[className] is required");
        }
        if (GlobalTrafficShapingHandler.class.getName().equals(className)) {
            TrafficShapingConfigurationProperties.GlobalConfig globalConfig = this.trafficShapingConfigurationProperties.toGlobal();
            return new GlobalTrafficShapingHandler(this.scheduledExecutorService, globalConfig.getWriteLimit(), globalConfig.getReadLimit(), globalConfig.getCheckInterval(), globalConfig.getMaxTime());
        }

        if (ChannelTrafficShapingHandler.class.getName().equals(className)) {
            TrafficShapingConfigurationProperties.ChannelConfig channelConfig = this.trafficShapingConfigurationProperties.toChannel();
            return new ChannelTrafficShapingHandler(channelConfig.getWriteLimit(), channelConfig.getReadLimit(), channelConfig.getCheckInterval(), channelConfig.getMaxTime());
        }

        if (GlobalChannelTrafficShapingHandler.class.getName().equals(className)) {
            TrafficShapingConfigurationProperties.GroupChannelConfig groupChannelConfig = this.trafficShapingConfigurationProperties.toGroupChannel();
            return new GlobalChannelTrafficShapingHandler(this.scheduledExecutorService,
                    groupChannelConfig.getWriteGlobalLimit(), groupChannelConfig.getReadGlobalLimit(),
                    groupChannelConfig.getWriteChannelLimit(), groupChannelConfig.getReadChannelLimit(),
                    groupChannelConfig.getCheckInterval(), groupChannelConfig.getMaxTime());
        }
        throw new IllegalArgumentException("invalid className: " + className);
    }
}
