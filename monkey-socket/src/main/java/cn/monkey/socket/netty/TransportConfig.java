package cn.monkey.socket.netty;

import cn.monkey.socket.util.MapUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class TransportConfig<T extends TransportConfig<T, B, C>, B extends TransportConfig<T, B, C>.Builder, C extends Channel> {

    TransportConfig() {
    }

    Consumer<Channel> channelCustomizer;

    Supplier<SocketAddress> socketAddress;

    Map<ChannelOption<?>, ?> channelOptions;

    Map<AttributeKey<?>, ?> channelAttributes;

    LoggingHandler loggingHandler;

    EventLoopGroup eventLoopGroup;

    Class<? extends C> channelClass;

    ShutDown shutDown = serverTransport -> {
        EventLoopGroup eventLoopGroup = serverTransport.configuration().eventLoopGroup();
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    };

    public Consumer<Channel> channelCustomizer() {
        return this.channelCustomizer;
    }

    @SuppressWarnings("unchecked")
    public T duplicate() {
        return (T) this;
    }

    public abstract B mutate();

    public Supplier<SocketAddress> socketAddress() {
        return this.socketAddress;
    }

    public Class<? extends C> channel() {
        return this.channelClass;
    }

    public Map<ChannelOption<?>, ?> channelOptions() {
        if (CollectionUtils.isEmpty(this.channelOptions)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.channelOptions);
    }

    public Map<AttributeKey<?>, ?> channelAttributes() {
        if (this.channelAttributes == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.channelAttributes);
    }

    protected LoggingHandler loggingHandler() {
        return this.loggingHandler;
    }

    protected EventLoopGroup eventLoopGroup() {
        return this.eventLoopGroup;
    }

    protected ShutDown shutDown() {
        return this.shutDown;
    }

    public class Builder {
        private final T instance;

        Builder(T instance) {
            this.instance = instance;
        }

        @SuppressWarnings("unchecked")
        public B duplicate() {
            return (B) this;
        }

        public B doOnChannelInit(Consumer<Channel> channelCustomizer) {
            if (this.instance.channelCustomizer == null) {
                this.instance.channelCustomizer = channelCustomizer;
                return duplicate();
            }
            this.instance.channelCustomizer = this.instance.channelCustomizer.andThen(channelCustomizer);
            return duplicate();
        }

        public B socketAddress(Supplier<SocketAddress> socketAddress) {
            this.instance.socketAddress = socketAddress;
            return duplicate();
        }


        public B channelClass(Class<? extends C> channelClass) {
            this.instance.channelClass = channelClass;
            return duplicate();
        }

        public B channelOptions(Map<ChannelOption<?>, ?> options) {
            this.instance.channelOptions = options;
            return duplicate();
        }


        public B attributes(Map<AttributeKey<?>, ?> attributes) {
            this.instance.channelAttributes = attributes;
            return duplicate();
        }


        public B loggingHandler(LoggingHandler loggingHandler) {
            this.instance.loggingHandler = loggingHandler;
            return duplicate();
        }

        public B eventLoopGroup(EventLoopGroup eventLoopGroup) {
            this.instance.eventLoopGroup = eventLoopGroup;
            return duplicate();
        }

        public B shutDown(ShutDown shutDown) {
            this.instance.shutDown = shutDown;
            return duplicate();
        }

        public T build() {
            return instance;
        }
    }

    @SuppressWarnings("unchecked")
    protected static <K, V> Map<K, V> updateMap(Map<K, V> parentMap, Object key, @Nullable Object value) {
        if (parentMap.isEmpty()) {
            return value == null ? parentMap : Collections.singletonMap((K) key, (V) value);
        } else {
            Map<K, V> attrs = new HashMap<>(MapUtils.calculateInitialCapacity(parentMap.size() + 1));
            attrs.putAll(parentMap);
            if (value == null) {
                attrs.remove((K) key);
            } else {
                attrs.put((K) key, (V) value);
            }
            return attrs;
        }
    }
}
