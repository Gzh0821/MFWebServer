package cn.monkey.gateway.stream.blacklist.async;

import cn.monkey.gateway.stream.blacklist.DefaultBlackEntity;
import cn.monkey.gateway.stream.blacklist.config.BlackListConfigurationProperties;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

public class DefaultBlackListFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(DefaultBlackListFilter.class);
    private final BlackEntityRepository blackEntityRepository;
    private final FailCounter failCounter;
    private final FailPredicate failPredicate;
    private final BlackKeyResolver blackKeyResolver;
    private final BlackListConfigurationProperties blackListConfigurationProperties;

    static final String BLACK_KEY_ATTRIBUTE_KEY = "black_key";

    public DefaultBlackListFilter(BlackEntityRepository blackEntityRepository,
                                  FailCounter failCounter, FailPredicate failPredicate, BlackKeyResolver blackKeyResolver, BlackListConfigurationProperties blackListConfigurationProperties) {
        this.blackEntityRepository = blackEntityRepository;
        this.failCounter = failCounter;
        this.failPredicate = failPredicate;
        this.blackKeyResolver = blackKeyResolver;
        this.blackListConfigurationProperties = blackListConfigurationProperties;
    }

    protected Mono<Void> checkBlackEntityAfterDoFilter(ServerWebExchange serverWebExchange, WebFilterChain chain) {
        String key = serverWebExchange.getAttribute(BLACK_KEY_ATTRIBUTE_KEY);
        return Mono.justOrEmpty(key)
                .flatMap(k -> this.failPredicate.test(serverWebExchange))
                .flatMap(testResult -> this.failCounter.incrementAndGet(key))
                .flatMap(countResult -> {
                    if (countResult > this.blackListConfigurationProperties.getFail().getMaxCount()) {
                        return this.blackEntityRepository.add(new DefaultBlackEntity(key));
                    }
                    return Mono.empty();
                })
                .then(chain.filter(serverWebExchange));
    }

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return this.blackKeyResolver.resolve(exchange)
                .flatMap(key -> {
                    exchange.getAttributes().put(BLACK_KEY_ATTRIBUTE_KEY, key);
                    return this.blackEntityRepository.containsKey(key);
                })
                .flatMap(containsResult -> {
                    if (containsResult) {
                        log.info("contains black key: {}", (String) exchange.getAttribute(BLACK_KEY_ATTRIBUTE_KEY));
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                        return response.setComplete();
                    }
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.defer(() -> this.checkBlackEntityAfterDoFilter(exchange, chain)))
                .onErrorResume((e) -> {
                    log.error("black list check error:\n", e);
                    ServerHttpResponse response = exchange.getResponse();
                    String message = e.getMessage();
                    response.setStatusCode(HttpStatus.BAD_GATEWAY);
                    if (!Strings.isNullOrEmpty(message)) {
                        response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(e.getMessage().getBytes()))));
                    }
                    return response.setComplete();
                });
    }
}
