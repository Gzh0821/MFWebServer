package cn.monkey.socket.server.msg;

import cn.monkey.data.pb.Command;
import cn.monkey.socket.netty.NettySessionManager;
import com.google.protobuf.InvalidProtocolBufferException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedisMsgConsumer implements MsgConsumer, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(RedisMsgConsumer.class);


    private final RedissonClient redissonClient;

    private final String msgQueueName;

    private final Scheduler scheduler = Schedulers.newSingle("msgScheduler");

    private final UserSessionRepository userSessionRepository;

    public RedisMsgConsumer(RedissonClient redissonClient,
                            String msgQueueName,
                            UserSessionRepository userSessionRepository) {
        this.redissonClient = redissonClient;
        this.msgQueueName = msgQueueName;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public void accept(Command.PkgGroup pkgGroup) {

    }

    @Override
    public void afterPropertiesSet() {
        RBlockingQueue<byte[]> blockingQueue = this.redissonClient.getBlockingQueue(this.msgQueueName);
        Flux.interval(Duration.of(1, TimeUnit.SECONDS.toChronoUnit()))
                .flatMap(l -> Mono.fromCompletionStage(blockingQueue.pollAsync(10))
                        .flatMapMany(Flux::fromIterable))
                .flatMap(bytes -> {
                    try {
                        return Mono.just(Command.PkgGroup.parseFrom(bytes));
                    } catch (InvalidProtocolBufferException e) {
                        return Mono.error(e);
                    }
                })
                .doOnNext(this)
                .doOnError(e -> log.error("msg subscribe error:\n", e))
                .subscribeOn(this.scheduler)
                .subscribe();
    }
}
