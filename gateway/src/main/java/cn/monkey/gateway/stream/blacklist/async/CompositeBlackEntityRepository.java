package cn.monkey.gateway.stream.blacklist.async;

import cn.monkey.gateway.stream.blacklist.BlackEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class CompositeBlackEntityRepository implements BlackEntityRepository {

    private final List<BlackEntityRepository> blackEntityRepositories;

    public CompositeBlackEntityRepository(List<BlackEntityRepository> blackEntityRepositories) {
        this.blackEntityRepositories = blackEntityRepositories;
    }

    @Override
    public Mono<Boolean> containsKey(String key) {
        return Flux.fromIterable(blackEntityRepositories)
                .flatMap(blackEntityRepository -> blackEntityRepository.containsKey(key))
                .any(b -> b);
    }

    @Override
    public Mono<BlackEntity> add(BlackEntity blackEntity) {
        return Flux.fromIterable(this.blackEntityRepositories)
                .next()
                .flatMap(blackEntityRepository -> blackEntityRepository.add(blackEntity));
    }
}
