package cn.monkey.gateway.stream.blacklist.async;

import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class HttpHeadersKeyResolver implements BlackKeyResolver {

    private final String[] headerKeys;

    public HttpHeadersKeyResolver(String... headerKeys) {
        this.headerKeys = headerKeys;
    }

    @Override
    public final Mono<String> resolve(ServerWebExchange serverWebExchange) {
        if (this.headerKeys == null || this.headerKeys.length == 0) {
            return Mono.empty();
        }
        HttpHeaders headers = serverWebExchange.getRequest().getHeaders();
        return this.resolve0(headers);
    }

    protected Mono<String> resolve0(HttpHeaders headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return Mono.empty();
        }
        return Flux.fromIterable(Arrays.asList(headerKeys))
                .flatMap(key -> {
                    for (String headerKey : headerKeys) {
                        List<String> s = headers.get(headerKey);
                        if (CollectionUtils.isEmpty(s)) {
                            continue;
                        }
                        return Mono.just(s.get(0));
                    }
                    return Mono.empty();
                }).next();
    }
}
