package cn.monkey.gateway.stream.blacklist.sync;

import com.google.common.base.Strings;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public abstract class BlackListChannelHandler extends ChannelDuplexHandler {

    private static final Logger log = LoggerFactory.getLogger(BlackListChannelHandler.class);

    protected final BlackEntityRepository blackListRepository;

    protected final BlackKeyResolver keyResolver;

    protected static final AttributeKey<String> BLACK_KEY_ATTRIBUTE_KEY = AttributeKey.newInstance("black_key");


    protected BlackListChannelHandler(BlackEntityRepository blackListRepository, BlackKeyResolver keyResolver) {
        this.blackListRepository = blackListRepository;
        this.keyResolver = keyResolver;
    }

    @Override
    public void channelRead(@NonNull ChannelHandlerContext ctx, @NonNull Object msg) throws Exception {
        String key = this.keyResolver.resolve(msg);
        if (!Strings.isNullOrEmpty(key)) {
            ctx.channel().attr(BLACK_KEY_ATTRIBUTE_KEY).set(key);
            if (this.blackListRepository.containsKey(key)) {
                log.error("bad request for ChannelHandlerContext id: {} key: {}  ",
                        ctx.channel().id().asLongText(), key);
                if (msg instanceof HttpMessage httpMessage) {
                    DefaultHttpResponse httpResponse = new DefaultHttpResponse(httpMessage.protocolVersion(), HttpResponseStatus.NOT_ACCEPTABLE);
                    ctx.channel().writeAndFlush(httpResponse);
                }
                ctx.channel().close();
                return;
            }
        }
        super.channelRead(ctx, msg);
    }

}
