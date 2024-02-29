package cn.monkey.gateway.stream.traffic;

import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import reactor.netty.http.server.HttpServer;

public class TrafficShapingCustomizer implements NettyServerCustomizer {

    private final TrafficShapingFactory trafficShapingFactory;

    private static final Logger log = LoggerFactory.getLogger(TrafficShapingCustomizer.class);

    public TrafficShapingCustomizer(TrafficShapingFactory trafficShapingFactory) {
        this.trafficShapingFactory = trafficShapingFactory;
    }

    @Override
    public HttpServer apply(HttpServer httpServer) {
        ChannelHandler channelHandler;
        try {
            channelHandler = this.trafficShapingFactory.getObject();
        } catch (Exception e) {
            log.warn("trafficShaping create error:\n", e);
            return httpServer;
        }
        return httpServer.doOnChannelInit((connectionObserver, channel, remoteAddress) -> channel.pipeline().addFirst(channelHandler));
    }
}
