package cn.monkey.gateway.stream.auth;


import cn.monkey.gateway.components.dsl.RequestPredicateContainer;
import cn.monkey.commons.data.vo.Result;
import cn.monkey.commons.data.vo.Results;
import cn.monkey.spring.web.HttpHeaderConstants;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    private final AuthClient authClient;

    private final RequestPredicateContainer requestPredicateContainer;

    public AuthFilter(AuthClient authClient,
                      RequestPredicateContainer requestPredicateContainer) {
        this.authClient = authClient;
        this.requestPredicateContainer = requestPredicateContainer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (log.isDebugEnabled()) {
            log.debug("auth filter, request headers: {}", request.getHeaders());
        }
        if (!this.match(request)) {
            return chain.filter(exchange);
        }
        Result<Void> result = this.checkedHeaders(request.getHeaders());
        if (!Results.isOK(result)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            String msg = result.getMsg();
            if (!Strings.isNullOrEmpty(msg)) {
                response.writeAndFlushWith(Flux.just(Flux.just(msg.getBytes()).map(response.bufferFactory()::wrap)));
            }
            return response.setComplete();
        }
        return this.authClient.check(exchange, chain);
    }

    private Result<Void> checkedHeaders(HttpHeaders headers) {
        List<String> authorization = headers.get(HttpHeaderConstants.AUTHORIZATION_KEY);
        List<String> platformId = headers.get(HttpHeaderConstants.PLATFORM_ID_KEY);
        if (CollectionUtils.isEmpty(authorization)) {
            return Results.fail("HttpHeaders: [ " + HttpHeaderConstants.AUTHORIZATION_KEY + " ] is required");
        }
        if (CollectionUtils.isEmpty(platformId)) {
            return Results.fail("HttpHeaders: [ " + HttpHeaderConstants.PLATFORM_ID_KEY + " ] is required");
        }
        return Results.ok();
    }


    protected boolean match(ServerHttpRequest request) {
        Predicate<ServerHttpRequest> requestPredicates = this.requestPredicateContainer.getRequestPredicate("");
        return requestPredicates.test(request);
    }
}
