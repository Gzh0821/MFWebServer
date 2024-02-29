package cn.monkey.gateway.components.dsl;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.function.Predicate;

public interface RequestPredicateContainer {
    Predicate<ServerHttpRequest> getRequestPredicate(String routeId);
}
