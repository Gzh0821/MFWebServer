package cn.monkey.socket.server;

import cn.monkey.commons.util.Timer;
import cn.monkey.data.pb.ChatCommandUtil;
import cn.monkey.data.pb.Command;
import cn.monkey.data.pb.CommandUtil;
import cn.monkey.socket.Dispatcher;
import cn.monkey.socket.Session;
import cn.monkey.socket.server.msg.data.User;
import cn.monkey.socket.server.util.ProtobufMsgUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RedisMqDispatcher implements Dispatcher<byte[]> {

    private static final Logger log = LoggerFactory.getLogger(RedisMqDispatcher.class);

    private final RedissonClient redissonClient;

    private final CurrentServerInfoRepository currentServerInfoRepository;

    private final Scheduler scheduler;

    private final Timer timer;

    private final LoadingCache<String, ReentrantLock> lockCache;

    private final int dataSyncServerSize;

    public RedisMqDispatcher(RedissonClient redissonClient,
                             CurrentServerInfoRepository currentServerInfoRepository,
                             Timer timer,
                             int dataSyncServerSize) {
        this.redissonClient = redissonClient;
        this.currentServerInfoRepository = currentServerInfoRepository;
        this.timer = timer;
        this.dataSyncServerSize = dataSyncServerSize;
        this.lockCache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(Duration.of(1, TimeUnit.SECONDS.toChronoUnit()))
                .build(new CacheLoader<>() {
                    @Override
                    @NonNull
                    public ReentrantLock load(@NonNull String key) {
                        return new ReentrantLock();
                    }
                });
        this.scheduler = Schedulers.newParallel(
                "redisMessageQueueDispatcherScheduler", 2);
    }

    @Override
    public void accept(Session session, byte[] s) {
        ReentrantLock lock = this.lockCache.getUnchecked(session.id());
        if (!lock.tryLock()) {
            return;
        }

        Mono.just(s)
                .flatMap(ss -> {
                    try {
                        return Mono.just(Command.Pkg.parseFrom(ss));
                    } catch (InvalidProtocolBufferException e) {
                        return Mono.error(e);
                    }
                })
                .map(pkg -> {
                    String serverId = this.currentServerInfoRepository.selectServerId();
                    Any from = ChatCommandUtil.from(serverId, ProtobufMsgUtil.user(session.get(User.KEY)));
                    return Tuples.of(serverId, CommandUtil.pkg(pkg.getCmdType(), from, null, pkg.getContent(), timer.getCurrentTimeMs()));
                })
                .flatMap(t -> {
                    RBlockingQueue<byte[]> blockingQueue = this.redissonClient.getBlockingQueue(this.hash(t.getT1()));
                    return Mono.fromCompletionStage(blockingQueue.putAsync(t.getT2().toByteArray()));
                })
                .doOnError(e -> log.error("msg subscribe error:\n", e))
                .doFinally(signalType -> lock.unlock())
                .subscribeOn(this.scheduler)
                .subscribe();
    }


    String hash(String targetId) {
        return String.valueOf(targetId.hashCode() % this.dataSyncServerSize);
    }
}
