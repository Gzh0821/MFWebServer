package cn.monkey.gateway.trace;

import reactor.core.publisher.Mono;

public interface TraceIdGenerator {
    default Mono<String> generate() {
        return Mono.empty();
    }
}
