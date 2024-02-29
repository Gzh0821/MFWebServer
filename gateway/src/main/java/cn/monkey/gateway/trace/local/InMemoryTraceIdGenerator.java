package cn.monkey.gateway.trace.local;

import cn.monkey.gateway.trace.TraceIdGenerator;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTraceIdGenerator implements TraceIdGenerator {
    private final AtomicLong offset;

    public InMemoryTraceIdGenerator(long startOffset) {
        this.offset = new AtomicLong(startOffset);
    }

    public InMemoryTraceIdGenerator() {
        this(0L);
    }

    @Override
    public Mono<String> generate() {
        return Mono.just(offset.incrementAndGet() + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID());
    }
}
