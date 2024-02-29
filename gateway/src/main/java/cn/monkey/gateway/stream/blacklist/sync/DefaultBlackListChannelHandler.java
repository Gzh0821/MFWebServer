package cn.monkey.gateway.stream.blacklist.sync;

import cn.monkey.gateway.stream.blacklist.DefaultBlackEntity;
import cn.monkey.gateway.stream.blacklist.config.BlackListConfigurationProperties;
import com.google.common.base.Strings;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class DefaultBlackListChannelHandler extends BlackListChannelHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultBlackListChannelHandler.class);

    protected final FailPredicate failPredicate;

    protected final FailCounter failCounter;

    protected final BlackListConfigurationProperties blackListConfigurationProperties;

    static final AttributeKey<Boolean> FAIL_COUNTED_SIGN = AttributeKey.newInstance("failCountedSign");


    public DefaultBlackListChannelHandler(BlackListConfigurationProperties blackListConfigurationProperties,
                                          BlackEntityRepository blackListRepository,
                                          BlackKeyResolver keyResolver, FailPredicate failPredicate, FailCounter failCounter) {
        super(blackListRepository, keyResolver);
        this.failPredicate = failPredicate;
        this.failCounter = failCounter;
        this.blackListConfigurationProperties = blackListConfigurationProperties;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String key = ctx.channel().attr(BLACK_KEY_ATTRIBUTE_KEY).get();
        Boolean failCountedSign = ctx.channel().attr(FAIL_COUNTED_SIGN).get();
        if (!Strings.isNullOrEmpty(key) && !Boolean.TRUE.equals(failCountedSign) && this.failPredicate.test(ctx, msg)) {
            if (this.failCounter.incrementAndGet(key) >= this.blackListConfigurationProperties.getFail().getMaxCount()) {
                if (log.isDebugEnabled()) {
                    log.warn("start add black key: {} ChannelHandlerContext id: {}", key, ctx.channel().id().asLongText());
                }
                this.blackListRepository.add(new DefaultBlackEntity(key));
                ctx.channel().attr(FAIL_COUNTED_SIGN).set(Boolean.TRUE);
            }
        }
        super.write(ctx, msg, promise);
    }
}
