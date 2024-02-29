package cn.monkey.gateway.stream.ratelimit.config;

import cn.monkey.gateway.components.dsl.RequestPredicateDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collection;
import java.util.Collections;

@ConfigurationProperties("spring.cloud.stream.rate-limit")
public class RateLimiterConfigurationProperties extends RouteRateLimiterDefinition {

    private Collection<RouteRateLimiterDefinition> clients = Collections.emptyList();

    private Collection<RequestPredicateDefinition> requestPredicates;

    public Collection<RouteRateLimiterDefinition> getClients() {
        return clients;
    }

    public Collection<RequestPredicateDefinition> getRequestPredicates() {
        return requestPredicates;
    }

    public void setRequestPredicates(Collection<RequestPredicateDefinition> requestPredicates) {
        this.requestPredicates = requestPredicates;
    }

    public void setClients(Collection<RouteRateLimiterDefinition> clients) {
        this.clients = clients;
    }
}
