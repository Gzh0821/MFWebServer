package cn.monkey.socket.server;

import cn.monkey.commons.util.Timer;
import cn.monkey.socket.Dispatcher;
import cn.monkey.socket.netty.DefaultNettySessionFactory;
import cn.monkey.socket.netty.DefaultNettySessionManager;
import cn.monkey.socket.netty.NettySessionFactory;
import cn.monkey.socket.netty.NettySessionManager;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerSocketConfiguration {
    @Bean
    SocketServerProperties socketServerProperties() {
        return new SocketServerProperties();
    }

    @Bean
    NettySessionFactory nettySessionFactory() {
        return new DefaultNettySessionFactory();
    }

    @Bean
    NettySessionManager sessionManager(NettySessionFactory nettySessionFactory) {
        return new DefaultNettySessionManager(nettySessionFactory);
    }

    @Bean
    CurrentServerInfoRepository currentServerInfoRepository() {
        return new InMemoryCurrentServerInfoRepository();
    }

    @Bean
    Dispatcher<byte[]> dispatcher(RedissonClient redissonClient,
                                  CurrentServerInfoRepository currentServerInfoRepository,
                                  Timer timer) {
        int dataSyncServerSize = 1;
        return new RedisMqDispatcher(redissonClient,
                currentServerInfoRepository,
                timer,
                dataSyncServerSize);
    }
}
