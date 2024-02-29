package cn.monkey.gateway.components.dsl;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.function.Predicate;

public class DefaultServerHttpRequestPredicateFactory implements ServerHttpRequestPredicateFactory {

    IterablePredicateFactory<RequestPredicateDefinition, ServerHttpRequest> iterableServerHttpRequestPredicateFactory;

    public DefaultServerHttpRequestPredicateFactory() {
        this.iterableServerHttpRequestPredicateFactory = new IterablePredicateFactory<>(RequestPredicateDefinition::getAnd, RequestPredicateDefinition::getOr);
        this.iterableServerHttpRequestPredicateFactory.setDelegateSupplier(() -> this);
    }

    @Override
    public Predicate<ServerHttpRequest> apply(RequestPredicateDefinition definition) {
        Predicate<ServerHttpRequest> p = new DefaultRequestPredicate(definition);
        p =p.and(this.iterableServerHttpRequestPredicateFactory.apply(definition));
        return Boolean.TRUE.equals(definition.isNegate())? p.negate():p;
    }
}
