package cn.monkey.gateway.stream.ratelimit.async;

import cn.monkey.spring.web.HttpHeaderConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class TokenKeyResolver implements ReactiveKeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (CollectionUtils.isEmpty(headers)) {
            return Mono.empty();
        }
        List<String> uids = headers.get(HttpHeaderConstants.AUTHORIZATION_KEY);
        return CollectionUtils.isEmpty(uids) ? Mono.empty() : Mono.just(uids.get(0));
    }
}
