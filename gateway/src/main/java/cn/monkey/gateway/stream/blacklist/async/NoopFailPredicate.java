package cn.monkey.gateway.stream.blacklist.async;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class NoopFailPredicate implements FailPredicate {

    public final static FailPredicate INSTANCE = new NoopFailPredicate();

    @Override
    public Mono<Boolean> test(ServerWebExchange serverWebExchange) {
        return Mono.empty();
    }
}
