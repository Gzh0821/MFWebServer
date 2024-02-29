package cn.monkey.gateway.trace.redisson;

import cn.monkey.gateway.trace.TraceIdGenerator;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class RedissonTraceIdGenerator implements TraceIdGenerator {

    private final RAtomicLong atomicLong;

    public static final String OFFSET_NAME = "trace_id_offset";

    public RedissonTraceIdGenerator(RedissonClient redissonClient) {
        atomicLong = redissonClient.getAtomicLong(OFFSET_NAME);
    }

    @Override
    public Mono<String> generate() {
        return Mono.fromCompletionStage(this.atomicLong.incrementAndGetAsync().toCompletableFuture())
                .map(i -> i + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID());
    }
}
