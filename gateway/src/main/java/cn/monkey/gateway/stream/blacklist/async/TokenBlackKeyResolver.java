package cn.monkey.gateway.stream.blacklist.async;

import cn.monkey.gateway.utils.NetUtils;
import cn.monkey.spring.web.HttpHeaderConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class TokenBlackKeyResolver implements BlackKeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        return Mono.justOrEmpty(NetUtils.getValFromHeaders(headers, HttpHeaderConstants.AUTHORIZATION_KEY));
    }
}
