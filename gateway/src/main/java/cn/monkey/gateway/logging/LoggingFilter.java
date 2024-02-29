package cn.monkey.gateway.logging;

import cn.monkey.gateway.components.dsl.DuplexServerWebPredicateContainer;
import cn.monkey.gateway.logging.data.LoggingEntity;
import cn.monkey.gateway.logging.data.Request;
import cn.monkey.gateway.logging.data.Response;
import cn.monkey.gateway.logging.utils.LoggingEntityUtils;
import cn.monkey.spring.web.HttpHeaderConstants;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    private static final String REQUEST_CACHE_KEY = "requestCacheKey";

    private static final String RESPONSE_CACHE_KEY = "responseCacheKey";

    private final List<HttpMessageReader<?>> messageReaders;
    private final LoggingRepository loggingRepository;
    private final DuplexServerWebPredicateContainer serverWebPredicateContainer;

    public LoggingFilter(List<HttpMessageReader<?>> messageReaders,
                         LoggingRepository loggingRepository,
                         DuplexServerWebPredicateContainer serverWebPredicateContainer) {
        this.messageReaders = CollectionUtils.isEmpty(messageReaders) ? HandlerStrategies.withDefaults().messageReaders() : messageReaders;
        this.loggingRepository = loggingRepository;
        this.serverWebPredicateContainer = serverWebPredicateContainer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = null;
        if (route != null) {
            routeId = route.getId();
        }
        if (!this.match(routeId, request)) {
            return chain.filter(exchange);
        }
        return this.filter0(exchange, chain);
    }

    protected boolean match(String routeId, ServerHttpRequest request) {
        Predicate<ServerHttpRequest> requestPredicates = this.serverWebPredicateContainer.getRequestPredicate(routeId);
        return requestPredicates.test(request);
    }

    protected boolean match(String routeId, ServerHttpResponse response) {
        Predicate<ServerHttpResponse> responsePredicates = this.serverWebPredicateContainer.getResponsePredicate(routeId);
        return responsePredicates.test(response);
    }

    private Mono<Void> filter0(ServerWebExchange exchange, GatewayFilterChain chain) {
        Request logRequest = LoggingEntityUtils.request(exchange, null);
        exchange.getAttributes().put(REQUEST_CACHE_KEY, logRequest);
        return ServerWebExchangeUtils.cacheRequestBodyAndRequest(exchange, request -> {
            final ServerRequest serverRequest = ServerRequest
                    .create(exchange.mutate().request(request).build(), messageReaders);
            return serverRequest.bodyToMono(DataBuffer.class).doOnNext(dataBuffer -> {
                byte[] content = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(content);
                DataBufferUtils.release(dataBuffer);
                Request cachedRequest = exchange.getAttribute(REQUEST_CACHE_KEY);
                if (cachedRequest != null) {
                    cachedRequest.setPayload(LoggingEntityUtils.copyByte(content));
                    exchange.getAttributes().put(REQUEST_CACHE_KEY, cachedRequest);
                }
            }).onErrorResume(throwable -> {
                log.error("cache request payload error:\n", throwable);
                return Mono.empty();
            }).then(Mono.defer(() -> this.afterRequestCached(exchange.mutate().request(request).build(), chain)));
        });
    }

    Mono<Void> afterRequestCached(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse cacheResponse = this.cacheResponsePayLoad(exchange);
        return chain.filter(exchange.mutate().response(cacheResponse).build())
                .then(Mono.defer(() -> this.tryBuildLoggingEntityAndSave(exchange)).onErrorResume(throwable -> {
                    log.error("cache response payload error:\n", throwable);
                    return Mono.empty();
                }));
    }

    private ServerHttpResponse cacheResponsePayLoad(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route == null ? null : route.getId();
        return new ServerHttpResponseDecorator(response) {
            @Override
            @NonNull
            public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                ServerHttpResponse httpResponse = this.getDelegate();
                if (!LoggingFilter.this.match(routeId, httpResponse)) {
                    return super.writeWith(body);
                }
                if (body instanceof Flux<? extends DataBuffer>) {
                    Response loggingResponse = LoggingEntityUtils.response(exchange, null);
                    exchange.getAttributes().put(RESPONSE_CACHE_KEY, loggingResponse);
                    return super.writeWith(Flux.from(body).buffer().map(dataBuffers -> {
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);
                        Response logResponse = exchange.getAttribute(RESPONSE_CACHE_KEY);
                        if (logResponse != null) {
                            logResponse.setPayload(LoggingEntityUtils.copyByte(content));
                        }
                        exchange.getAttributes().put(RESPONSE_CACHE_KEY, logResponse);
                        return dataBufferFactory.wrap(content);
                    }));
                }
                return super.writeWith(body);
            }
        };
    }


    private Mono<Void> tryBuildLoggingEntityAndSave(ServerWebExchange exchange) {
        Request request = exchange.getAttribute(REQUEST_CACHE_KEY);
        Response response = exchange.getAttribute(RESPONSE_CACHE_KEY);
        if (request == null || response == null) {
            return Mono.empty();
        }
        try {
            LoggingEntity loggingEntity = new LoggingEntity();
            loggingEntity.setRequest(request);
            loggingEntity.setResponse(response);
            ServerHttpRequest serverHttpRequest = exchange.getRequest();
            String traceId = serverHttpRequest.getHeaders().getFirst(HttpHeaderConstants.TRACE_ID_KEY);
            loggingEntity.setTraceId(traceId);
            return this.loggingRepository.saveAndFlush(loggingEntity).doFinally(signalType -> {
                exchange.getAttributes().remove(REQUEST_CACHE_KEY);
                exchange.getAttributes().remove(RESPONSE_CACHE_KEY);
            });
        } catch (Exception e) {
            log.error("mergeRequestAndResponseThenSave error:\n", e);
            return Mono.empty();
        } finally {
            exchange.getAttributes().remove(REQUEST_CACHE_KEY);
            exchange.getAttributes().remove(RESPONSE_CACHE_KEY);
        }
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
