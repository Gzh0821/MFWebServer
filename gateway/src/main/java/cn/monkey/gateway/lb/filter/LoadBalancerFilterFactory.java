package cn.monkey.gateway.lb.filter;

import cn.monkey.gateway.lb.config.LoadBalancerFilterDefinition;

public interface LoadBalancerFilterFactory {

    String getName();

    LoadBalancerFilter create(LoadBalancerFilterDefinition loaderBalancerDefinition);
}
