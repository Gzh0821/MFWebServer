package cn.monkey.gateway.stream.auth;

import cn.monkey.gateway.components.dsl.RequestPredicateContainer;
import cn.monkey.gateway.components.dsl.RequestPredicateDefinition;
import cn.monkey.gateway.components.dsl.ServerHttpRequestPredicateFactory;
import cn.monkey.gateway.stream.auth.config.AuthConfigProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.function.Predicate;

public class DefaultAuthRequestPredicateContainer implements RequestPredicateContainer, InitializingBean {

    private final ServerHttpRequestPredicateFactory serverHttpRequestPredicateFactory;

    private final AuthConfigProperties authConfigProperties;
    private volatile Predicate<ServerHttpRequest> filter;

    public DefaultAuthRequestPredicateContainer(ServerHttpRequestPredicateFactory serverHttpRequestPredicateFactory,
                                                AuthConfigProperties authConfigProperties) {
        this.serverHttpRequestPredicateFactory = serverHttpRequestPredicateFactory;
        this.authConfigProperties = authConfigProperties;
        this.filter = serverHttpRequest -> true;
    }

    @Override
    public Predicate<ServerHttpRequest> getRequestPredicate(String routeId) {
        return this.filter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RequestPredicateDefinition filter = authConfigProperties.getFilter();
        if (filter == null) {
            return;
        }
        this.filter = this.serverHttpRequestPredicateFactory.apply(filter);
    }
}
