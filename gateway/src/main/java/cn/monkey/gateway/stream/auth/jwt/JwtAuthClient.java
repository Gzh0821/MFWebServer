package cn.monkey.gateway.stream.auth.jwt;

import cn.monkey.gateway.stream.auth.AuthClient;
import cn.monkey.commons.bean.JwtAuthorizationKeyManager;
import cn.monkey.spring.web.HttpHeaderConstants;
import com.google.common.base.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtAuthClient implements AuthClient {

    private final JwtAuthorizationKeyManager authorizationKeyManager;

    public JwtAuthClient(JwtAuthorizationKeyManager authorizationKeyManager) {
        this.authorizationKeyManager = authorizationKeyManager;
    }

    @Override
    @NonNull
    public Mono<Void> check(@NonNull ServerWebExchange exchange,
                            @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String first = request.getHeaders().getFirst(HttpHeaderConstants.AUTHORIZATION_KEY);
        if (Strings.isNullOrEmpty(first)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        try {
            authorizationKeyManager.decrypt(first);
            return chain.filter(exchange);
        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
    }
}
