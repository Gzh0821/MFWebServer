package cn.monkey.gateway.stream.blacklist.async;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface FailPredicate {
    Mono<Boolean> test(ServerWebExchange exchange);
}
