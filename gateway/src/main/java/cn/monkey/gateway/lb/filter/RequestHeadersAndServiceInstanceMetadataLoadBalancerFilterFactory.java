package cn.monkey.gateway.lb.filter;

import cn.monkey.gateway.lb.config.LoadBalancerFilterDefinition;
import cn.monkey.gateway.lb.utils.NameUtils;

public class RequestHeadersAndServiceInstanceMetadataLoadBalancerFilterFactory implements LoadBalancerFilterFactory {

    @Override
    public String getName() {
        return NameUtils.normalizeFilterFactoryName(this.getClass());
    }

    @Override
    public LoadBalancerFilter create(LoadBalancerFilterDefinition loaderBalancerDefinition) {
        return new RequestHeadersAndServiceInstanceMetadataLoadBalancerFilter(loaderBalancerDefinition);
    }
}
