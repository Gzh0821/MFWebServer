package cn.monkey.gateway.logging.config;

import cn.monkey.gateway.components.dsl.RequestPredicateDefinition;
import cn.monkey.gateway.components.dsl.ResponsePredicateDefinition;
import org.springframework.cloud.gateway.support.HasRouteId;

import java.util.List;

public class LoggingDefinition implements HasRouteId {
    private String routeId;
    private List<RequestPredicateDefinition> requestPredicates;

    private List<ResponsePredicateDefinition> responsePredicates;

    public List<RequestPredicateDefinition> getRequestPredicates() {
        return requestPredicates;
    }

    public void setRequestPredicates(List<RequestPredicateDefinition> requestPredicates) {
        this.requestPredicates = requestPredicates;
    }

    public List<ResponsePredicateDefinition> getResponsePredicates() {
        return responsePredicates;
    }

    public void setResponsePredicates(List<ResponsePredicateDefinition> responsePredicates) {
        this.responsePredicates = responsePredicates;
    }

    @Override
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public String getRouteId() {
        return this.routeId;
    }
}
