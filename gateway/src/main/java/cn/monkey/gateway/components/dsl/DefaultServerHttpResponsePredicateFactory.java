package cn.monkey.gateway.components.dsl;

import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.function.Predicate;

public class DefaultServerHttpResponsePredicateFactory implements ServerHttpResponsePredicateFactory {

    private final IterablePredicateFactory<ResponsePredicateDefinition, ServerHttpResponse> iterableServerHttpResponsePredicateFactory;

    public DefaultServerHttpResponsePredicateFactory() {
        this.iterableServerHttpResponsePredicateFactory = new IterablePredicateFactory<>(ResponsePredicateDefinition::getAnd, ResponsePredicateDefinition::getOr);
        this.iterableServerHttpResponsePredicateFactory.setDelegateSupplier(() -> this);
    }

    @Override
    public Predicate<ServerHttpResponse> apply(ResponsePredicateDefinition definition) {
        Predicate<ServerHttpResponse> p = new DefaultResponsePredicate(definition);
        if (Boolean.TRUE.equals(definition.isNegate())) {
            return p.and(iterableServerHttpResponsePredicateFactory.apply(definition)).negate();
        } else {
            return p.and(iterableServerHttpResponsePredicateFactory.apply(definition));
        }
    }
}
