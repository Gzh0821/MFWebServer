package cn.monkey.gateway.stream.blacklist.async;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class CompositeBlackKeyResolver implements BlackKeyResolver {

    private final BlackKeyResolver previous;
    private final BlackKeyResolver next;

    CompositeBlackKeyResolver(BlackKeyResolver previous, BlackKeyResolver next) {
        this.previous = previous;
        this.next = next;
    }

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return this.previous.resolve(exchange).switchIfEmpty(next.resolve(exchange));
    }
}
