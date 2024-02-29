package cn.monkey.gateway.stream.blacklist.async;

import reactor.core.publisher.Mono;

public interface FailCounter {
    Mono<Long> getCount(String key);

    default Mono<Long> incrementAndGet(String key) {
        return this.incrementAndGet(key, 1L);
    }

    Mono<Long> incrementAndGet(String key, long delta);
}
