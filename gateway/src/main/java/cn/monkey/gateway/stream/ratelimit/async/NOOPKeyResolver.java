package cn.monkey.gateway.stream.ratelimit.async;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class NOOPKeyResolver implements ReactiveKeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.empty();
    }
}
