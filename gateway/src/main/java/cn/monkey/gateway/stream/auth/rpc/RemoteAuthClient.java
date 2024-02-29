package cn.monkey.gateway.stream.auth.rpc;

import cn.monkey.gateway.stream.auth.AbstractAuthClient;
import cn.monkey.commons.data.vo.Result;
import cn.monkey.commons.data.vo.Results;
import cn.monkey.gateway.stream.auth.config.AuthConfigProperties;
import cn.monkey.gateway.utils.NetUtils;
import com.google.common.base.Strings;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.nio.ByteBuffer;

public class RemoteAuthClient extends AbstractAuthClient {

    private final WebClient webClient;

    private final AuthConfigProperties authConfigProperties;

    public RemoteAuthClient(WebClient.Builder webClientBuilder,
                            AuthConfigProperties authConfigProperties) {
        this.webClient = webClientBuilder.build();
        this.authConfigProperties = authConfigProperties;
    }

    protected Mono<Result<?>> decode(ClientResponse clientResponse, byte[] body) {
        HttpHeaders httpHeaders = clientResponse.headers().asHttpHeaders();
        MediaType contentType = httpHeaders.getContentType();
        if (isJsonContentType(contentType)) {
            return Mono.just(Results.fromJsonStr(new String(body), Void.class));
        }

        // TODO other media types
        return Mono.just(Results.ok());
    }

    @Override
    @NonNull
    public Mono<Void> check(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        return webClient.post()
                .uri(this.authConfigProperties.getRequest().getPath())
                .headers(httpHeaders -> httpHeaders.addAll(NetUtils.buildJsonContentHeaders(request)))
                .bodyValue(NetUtils.buildBody(request))
                .exchangeToMono(response -> {
                    HttpStatusCode httpStatusCode = response.statusCode();
                    ServerHttpResponse serverHttpResponse = exchange.getResponse();
                    if (HttpStatus.OK == httpStatusCode) {
                        return response.bodyToMono(byte[].class)
                                .flatMap(bytes -> this.decode(response, bytes)
                                        .flatMap(result -> {
                                            if (!Results.isOK(result)) {
                                                this.logErrorPath(exchange, new String(bytes));
                                                serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                                                serverHttpResponse.writeAndFlushWith(Flux.just(ByteBufFlux.just(serverHttpResponse.bufferFactory().wrap(bytes))));
                                                return serverHttpResponse.setComplete();
                                            }
                                            return Mono.empty();
                                        })
                                        .switchIfEmpty(chain.filter(exchange)));
                    }
                    this.logErrorPath(exchange, "bad httpCode: " + httpStatusCode);
                    serverHttpResponse.setStatusCode(response.statusCode());
                    ClientResponse.Headers webClientHeaders = response.headers();
                    if (!serverHttpResponse.isCommitted()) {
                        serverHttpResponse.beforeCommit(() -> Mono.fromRunnable(() -> serverHttpResponse.getHeaders().addAll(webClientHeaders.asHttpHeaders())));
                        // do not change response information...
                        return serverHttpResponse.writeWith(response.bodyToFlux(ByteBuffer.class).map(serverHttpResponse.bufferFactory()::wrap));
                    }
                    return serverHttpResponse.setComplete();
                })
                .onErrorResume(e -> {
                    this.logErrorPath(exchange, e.getMessage());
                    ServerHttpResponse serverHttpResponse = exchange.getResponse();
                    if (e instanceof ResponseStatusException rse) {
                        HttpStatusCode statusCode = rse.getStatusCode();
                        serverHttpResponse.setStatusCode(statusCode);
                        serverHttpResponse.beforeCommit(() -> Mono.fromRunnable(() -> serverHttpResponse.getHeaders().addAll(rse.getHeaders())));
                        serverHttpResponse.writeAndFlushWith(Flux.just(ByteBufFlux.just(serverHttpResponse.bufferFactory().wrap(rse.getMessage().getBytes()))));
                        return serverHttpResponse.setComplete();
                    }
                    serverHttpResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
                    String message = e.getMessage();
                    if (!Strings.isNullOrEmpty(message)) {
                        serverHttpResponse.writeAndFlushWith(Flux.just(ByteBufFlux.just(serverHttpResponse.bufferFactory().wrap(e.getMessage().getBytes()))));
                    }
                    return serverHttpResponse.setComplete();
                });

    }
}
