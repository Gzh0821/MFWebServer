package cn.monkey.gateway.stream.blacklist.sync;

import cn.monkey.gateway.stream.blacklist.config.BlackListConfigurationProperties;
import io.netty.channel.ChannelHandler;

public class DefaultBlackListChannelHandlerFactory implements BlackListChannelHandlerFactory {

    private final BlackListConfigurationProperties blackListConfigurationProperties;

    private final FailCounter failCounter;
    private final FailPredicate failPredicate;
    private final BlackEntityRepository blackListRepository;

    private final BlackKeyResolver keyResolver;

    public DefaultBlackListChannelHandlerFactory(BlackListConfigurationProperties blackListConfigurationProperties,
                                                 FailCounter failCounter, FailPredicate failPredicate, BlackEntityRepository blackListRepository, BlackKeyResolver keyResolver) {
        this.blackListConfigurationProperties = blackListConfigurationProperties;
        this.failCounter = failCounter;
        this.failPredicate = failPredicate;
        this.blackListRepository = blackListRepository;
        this.keyResolver = keyResolver;
    }

    @Override
    public ChannelHandler getObject() throws Exception {
        return new DefaultBlackListChannelHandler(this.blackListConfigurationProperties, this.blackListRepository, this.keyResolver, this.failPredicate, this.failCounter);
    }
}
