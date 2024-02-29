package cn.monkey.gateway.stream.ratelimit.async;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public class RequestMatchKeyResolver implements ReactiveKeyResolver {

    private final Predicate<ServerHttpRequest> requestPredicate;

    public RequestMatchKeyResolver(Predicate<ServerHttpRequest> requestPredicate) {
        this.requestPredicate = requestPredicate;
    }

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        if (this.match(exchange.getRequest())) {
            return ReactiveKeyResolver.super.resolve(exchange);
        }
        return Mono.empty();
    }

    private boolean match(ServerHttpRequest request) {
        return this.requestPredicate.test(request);
    }
}
