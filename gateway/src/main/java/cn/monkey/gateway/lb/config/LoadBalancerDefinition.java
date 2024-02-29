package cn.monkey.gateway.lb.config;

import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.lang.Nullable;

import java.util.List;

public class LoadBalancerDefinition implements HasRouteId {
    private List<LoadBalancerFilterDefinition> filters;
    private String routeId;

    private String strategy = Strategy.DEFAULT;

    public List<LoadBalancerFilterDefinition> getFilters() {
        return filters;
    }

    public void setFilters(List<LoadBalancerFilterDefinition> filters) {
        this.filters = filters;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @Override
    @Nullable
    public String getRouteId() {
        return this.routeId;
    }
}
