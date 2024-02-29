package cn.monkey.gateway.stream.blacklist.async;

import cn.monkey.gateway.stream.blacklist.BlackEntity;
import reactor.core.publisher.Mono;

public interface BlackEntityRepository {

    default Mono<Boolean> containsKey(String key) {
        return Mono.empty();
    }

    default Mono<BlackEntity> add(BlackEntity blackEntity) {
        return Mono.empty();
    }

    default Mono<BlackEntity> remove(BlackEntity blackEntity) {
        return Mono.empty();
    }
}
