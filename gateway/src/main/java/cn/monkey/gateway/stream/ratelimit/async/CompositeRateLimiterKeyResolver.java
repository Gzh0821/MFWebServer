package cn.monkey.gateway.stream.ratelimit.async;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class CompositeRateLimiterKeyResolver implements ReactiveKeyResolver {

    private final ReactiveKeyResolver previous;

    private final ReactiveKeyResolver next;

    public CompositeRateLimiterKeyResolver(ReactiveKeyResolver previous, ReactiveKeyResolver next) {
        this.previous = previous;
        this.next = next;
    }

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return this.previous.resolve(exchange).switchIfEmpty(this.next.resolve(exchange));
    }
}
