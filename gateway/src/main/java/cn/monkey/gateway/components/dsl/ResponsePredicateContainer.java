package cn.monkey.gateway.components.dsl;

import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.function.Predicate;

public interface ResponsePredicateContainer {
    Predicate<ServerHttpResponse> getResponsePredicate(String routeId);

}
