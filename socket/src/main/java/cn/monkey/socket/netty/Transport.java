package cn.monkey.socket.netty;

import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Objects;

public abstract class Transport<S extends Transport<S, CONF>, CONF extends TransportConfig<CONF, ?, ?>> {

    private static final Logger log = LoggerFactory.getLogger(Transport.class);

    @SuppressWarnings("unchecked")
    protected S duplicate() {
        return (S) this;
    }

    public abstract CONF configuration();

    public S eventLoopGroup(EventLoopGroup eventLoopGroup) {
        configuration().mutate().eventLoopGroup(eventLoopGroup);
        return duplicate();
    }

    public <V> S attr(AttributeKey<V> key, V val) {
        Objects.requireNonNull(key, "key");
        S dup = duplicate();
        CONF configuration = dup.configuration();
        configuration.mutate().attributes(TransportConfig.updateMap(configuration.channelAttributes, key, val));
        return dup;
    }

    public <O> S channelOption(ChannelOption<O> key, @Nullable O val) {
        Objects.requireNonNull(key, "key");
        // Reference comparison is deliberate
        if (ChannelOption.AUTO_READ == key) {
            if (val instanceof Boolean && Boolean.TRUE.equals(val)) {
                log.error("ChannelOption.AUTO_READ is configured to be [false], it cannot be set to [true]");
            }
            return duplicate();
        }
        S dup = duplicate();
        Map<ChannelOption<?>, ?> channelOptionMap = TransportConfig.updateMap(configuration().channelOptions(), key, val);
        dup.configuration().mutate().channelOptions(channelOptionMap);
        return dup;
    }


}
