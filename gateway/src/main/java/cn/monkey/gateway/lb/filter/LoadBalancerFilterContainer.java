package cn.monkey.gateway.lb.filter;

import java.util.List;

public interface LoadBalancerFilterContainer {
    List<LoadBalancerFilter> getSortedFilters(String routeId);
}
