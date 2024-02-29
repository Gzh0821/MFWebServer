package cn.monkey.gateway.stream.ratelimit.async;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static cn.monkey.gateway.utils.NetUtils.getIp;

public class IPKeyResolver implements ReactiveKeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String ip = getIp(exchange.getRequest().getHeaders());
        if (ip == null) {
            return Mono.empty();
        }
        return Mono.just(ip);
    }
}
