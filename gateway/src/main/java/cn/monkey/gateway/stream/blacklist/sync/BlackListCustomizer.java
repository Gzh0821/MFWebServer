package cn.monkey.gateway.stream.blacklist.sync;

import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import reactor.netty.NettyPipeline;
import reactor.netty.http.server.HttpServer;

public class BlackListCustomizer implements NettyServerCustomizer {

    private final BlackListChannelHandlerFactory blackListChannelHandlerFactory;

    private static final Logger log = LoggerFactory.getLogger(BlackListCustomizer.class);

    public BlackListCustomizer(BlackListChannelHandlerFactory trafficShapingFactory) {
        this.blackListChannelHandlerFactory = trafficShapingFactory;
    }

    @Override
    public HttpServer apply(HttpServer httpServer) {
        ChannelHandler channelHandler;
        try {
            channelHandler = this.blackListChannelHandlerFactory.getObject();
        } catch (Exception e) {
            log.warn("blackList channelHandler create error:\n", e);
            return httpServer;
        }
        // add after httpClientCodec
        return httpServer.doOnChannelInit((connectionObserver, channel, remoteAddress) -> channel.pipeline().addAfter(NettyPipeline.HttpCodec, "blackListChannelHandler", channelHandler));
    }
}
