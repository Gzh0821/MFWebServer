package cn.monkey.gateway.stream.ratelimit.async;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ReactiveKeyResolver extends KeyResolver, cn.monkey.gateway.stream.ratelimit.KeyResolver {
    @Override
    default Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(EMPTY_KEY);
    }
}
