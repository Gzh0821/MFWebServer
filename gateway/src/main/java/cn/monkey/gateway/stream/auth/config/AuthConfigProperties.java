package cn.monkey.gateway.stream.auth.config;

import cn.monkey.gateway.components.dsl.Request;
import cn.monkey.gateway.components.dsl.RequestPredicateDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = AuthConfigProperties.CONFIGURATION_PROPERTIES_PREFIX)
public class AuthConfigProperties {

    public static final String CONFIGURATION_PROPERTIES_PREFIX = "spring.cloud.gateway.stream.auth";

    private Request request;

    private RequestPredicateDefinition filter;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public RequestPredicateDefinition getFilter() {
        return filter;
    }

    public void setFilter(RequestPredicateDefinition filters) {
        this.filter = filters;
    }
}
