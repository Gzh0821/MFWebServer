package cn.monkey.gateway.stream.auth;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public interface AuthClient {
    Mono<Void> check(ServerWebExchange exchange, WebFilterChain chain);
}
