package cn.monkey.gateway.lb.filter;

import cn.monkey.gateway.lb.config.LoadBalancerFilterDefinition;
import cn.monkey.gateway.lb.utils.NameUtils;

public class SpelLoadBalancerFilterFactory implements LoadBalancerFilterFactory {
    @Override
    public String getName() {
        return NameUtils.normalizeFilterFactoryName(this.getClass());
    }
    @Override
    public LoadBalancerFilter create(LoadBalancerFilterDefinition definition) {
        return new SpelLoadBalancerFilter(definition);
    }
}
