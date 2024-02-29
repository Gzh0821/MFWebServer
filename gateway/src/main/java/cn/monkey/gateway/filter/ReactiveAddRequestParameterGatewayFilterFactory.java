package cn.monkey.gateway.filter;

import cn.monkey.gateway.components.dsl.Request;
import cn.monkey.gateway.components.dsl.RequestPredicateDefinition;
import cn.monkey.gateway.components.dsl.ServerHttpRequestPredicateFactory;
import cn.monkey.commons.data.KVPair;
import cn.monkey.commons.data.vo.Results;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;

/**
 * use rpc request
 */
public class ReactiveAddRequestParameterGatewayFilterFactory extends AbstractGatewayFilterFactory<ReactiveAddRequestParameterGatewayFilterFactory.RequestConfig> {

    private static final Logger log = LoggerFactory.getLogger(ReactiveAddRequestParameterGatewayFilterFactory.class);
    protected final WebClient.Builder webClientBuilder;

    protected final ServerHttpRequestPredicateFactory serverHttpRequestPredicateFactory;
    protected final Gson gson;

    public ReactiveAddRequestParameterGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                                           ServerHttpRequestPredicateFactory serverHttpRequestPredicateFactory) {
        this.webClientBuilder = webClientBuilder;
        this.serverHttpRequestPredicateFactory = serverHttpRequestPredicateFactory;
        this.gson = new Gson();
    }

    static class KVPairList extends ArrayList<KVPair<String, String>> {

    }

    @Override
    public GatewayFilter apply(RequestConfig config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!this.match(request, config.getAddParamsRequest())) {
                return chain.filter(exchange);
            }
            return this.webClientBuilder.build()
                    .method(HttpMethod.valueOf(config.getGetParamsRequest().getMethod()))
                    .uri(config.getGetParamsRequest().getPath())
                    .headers(headers -> headers.addAll(this.buildHeaders(request)))
                    .exchangeToMono(clientResponse -> {
                        HttpStatusCode httpStatusCode = clientResponse.statusCode();
                        if (httpStatusCode != HttpStatus.OK) {
                            return chain.filter(exchange);
                        }
                        return clientResponse.bodyToMono(String.class)
                                .map(s -> Results.fromJsonStr(s, gson, KVPairList.class))
                                .flatMap(result -> {
                                    if (!Results.isOK(result)) {
                                        return chain.filter(exchange);
                                    }
                                    return chain.filter(this.exchange(exchange, result.getData()));
                                });
                    });
        };
    }

    private boolean match(ServerHttpRequest request, RequestPredicateDefinition addParamsRequest) {
        Predicate<ServerHttpRequest> predicate = this.serverHttpRequestPredicateFactory.apply(addParamsRequest);
        return predicate.test(request);
    }

    Map<String, String> getQuery(String query) {
        if (Strings.isNullOrEmpty(query)) {
            return Collections.emptyMap();
        }
        Map<String, String> queryMap = new HashMap<>();
        for (String s : query.split("&")) {
            String[] ss = s.split("=");
            if (ss.length != 2) {
                continue;
            }
            queryMap.put(ss[0], ss[1]);
        }
        return queryMap;
    }

    ServerWebExchange exchange(ServerWebExchange exchange, KVPairList kvPairList) {
        URI uri = exchange.getRequest().getURI();
        StringBuilder query = new StringBuilder();
        String originalQuery = uri.getRawQuery();
        Map<String, String> queryMap = this.getQuery(originalQuery);
        if (StringUtils.hasText(originalQuery)) {
            query.append(originalQuery);
            if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                query.append('&');
            }
        }
        for (KVPair<String, String> kvPair : kvPairList) {
            if (queryMap.containsKey(kvPair.getK())) {
                continue;
            }
            query.append(kvPair.getK()).append('=').append(kvPair.getV());
        }
        try {
            URI newUri = UriComponentsBuilder.fromUri(uri).replaceQuery(query.toString()).build(true).toUri();
            ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();
            return exchange.mutate().request(request).build();
        } catch (RuntimeException ex) {
            log.error("Invalid URI query: \"" + query + "\"", ex);
            return exchange;
        }
    }

    protected HttpHeaders buildHeaders(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, List<String>> e : entries) {
            String key = e.getKey();
            if (HttpHeaders.CONTENT_TYPE.equals(key)) {
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            } else {
                httpHeaders.put(key, e.getValue());
            }
        }
        return httpHeaders;
    }


    public static class RequestConfig {
        private RequestPredicateDefinition addParamsRequest;
        private Request getParamsRequest;

        public RequestPredicateDefinition getAddParamsRequest() {
            return addParamsRequest;
        }

        public void setAddParamsRequest(RequestPredicateDefinition addParamsRequest) {
            this.addParamsRequest = addParamsRequest;
        }

        public Request getGetParamsRequest() {
            return getParamsRequest;
        }

        public void setGetParamsRequest(Request getParamsRequest) {
            this.getParamsRequest = getParamsRequest;
        }
    }
}
