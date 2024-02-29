package cn.monkey.gateway.logging.config;

import cn.monkey.gateway.components.dsl.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class DefaultLoggingServerWebPredicateContainer implements DuplexServerWebPredicateContainer {

    private final ServerHttpRequestPredicateFactory requestPredicateFactory;

    private final ServerHttpResponsePredicateFactory serverHttpResponsePredicateFactory;

    private final LoggingConfigProperties loggingConfigProperties;

    private final Map<String, Predicate<ServerHttpRequest>> requestPredicateMap;

    private final Map<String, Predicate<ServerHttpResponse>> responsePredicateMap;

    public DefaultLoggingServerWebPredicateContainer(ServerHttpRequestPredicateFactory requestPredicateFactory,
                                                     ServerHttpResponsePredicateFactory serverHttpResponsePredicateFactory,
                                                     LoggingConfigProperties loggingConfigProperties) {
        this.requestPredicateFactory = requestPredicateFactory;
        this.serverHttpResponsePredicateFactory = serverHttpResponsePredicateFactory;
        this.loggingConfigProperties = loggingConfigProperties;
        this.requestPredicateMap = new ConcurrentHashMap<>();
        this.responsePredicateMap = new ConcurrentHashMap<>();
    }


    @Override
    public Predicate<ServerHttpRequest> getRequestPredicate(String routeId) {
        final String id = routeId == null ? "" : routeId;
        return this.requestPredicateMap.computeIfAbsent(id, (key) -> {
            List<LoggingDefinition> clients = this.loggingConfigProperties.getClients();
            List<RequestPredicateDefinition> requestPredicateDefinitions = null;
            if (!CollectionUtils.isEmpty(clients)) {
                for (LoggingDefinition loggingDefinition : clients) {
                    if (id.equals(loggingDefinition.getRouteId())) {
                        requestPredicateDefinitions = loggingDefinition.getRequestPredicates();
                        break;
                    }
                }
            }
            if (CollectionUtils.isEmpty(requestPredicateDefinitions)) {
                requestPredicateDefinitions = this.loggingConfigProperties.getRequestPredicates();
            }
            return requestPredicateDefinitions.stream()
                    .map(this.requestPredicateFactory)
                    .reduce(Predicate::and).orElse(serverHttpRequest -> true);
        });
    }

    @Override
    public Predicate<ServerHttpResponse> getResponsePredicate(String routeId) {
        final String id = routeId == null ? "" : routeId;
        return this.responsePredicateMap.computeIfAbsent(id, (key) -> {
            List<LoggingDefinition> clients = this.loggingConfigProperties.getClients();
            List<ResponsePredicateDefinition> responsePredicateDefinitions = null;
            if (!CollectionUtils.isEmpty(clients)) {
                for (LoggingDefinition loggingDefinition : clients) {
                    if (id.equals(loggingDefinition.getRouteId())) {
                        responsePredicateDefinitions = loggingDefinition.getResponsePredicates();
                        break;
                    }
                }
            }
            if (CollectionUtils.isEmpty(responsePredicateDefinitions)) {
                responsePredicateDefinitions = this.loggingConfigProperties.getResponsePredicates();
            }
            return responsePredicateDefinitions.stream()
                    .map(this.serverHttpResponsePredicateFactory)
                    .reduce(Predicate::and).orElse(serverHttpResponse -> true);
        });
    }
}
