package cn.monkey.gateway.stream.blacklist.async;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface BlackKeyResolver {
    default Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.empty();
    }
}
