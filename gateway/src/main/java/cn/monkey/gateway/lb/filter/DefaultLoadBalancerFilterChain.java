package cn.monkey.gateway.lb.filter;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Supplier;

public class DefaultLoadBalancerFilterChain implements LoadBalancerFilterChain {

    private final int index;

    private final Supplier<List<LoadBalancerFilter>> loadBalancerFilterSupplier;

    public DefaultLoadBalancerFilterChain(int index, DefaultLoadBalancerFilterChain parent) {
        this.index = index;
        this.loadBalancerFilterSupplier = parent.loadBalancerFilterSupplier;
    }

    public DefaultLoadBalancerFilterChain(Supplier<List<LoadBalancerFilter>> loadBalancerFilterSupplier) {
        this.index = 0;
        this.loadBalancerFilterSupplier = loadBalancerFilterSupplier;
    }

    @Override
    public Flux<ServiceInstance> doFilter(Request<?> request, List<ServiceInstance> serviceInstances) {
        List<LoadBalancerFilter> loadBalancerFilters = loadBalancerFilterSupplier.get();
        if (loadBalancerFilters != null && this.index < loadBalancerFilters.size()) {
            LoadBalancerFilter loadBalancerFilter = loadBalancerFilters.get(this.index);
            DefaultLoadBalancerFilterChain filterChain = new DefaultLoadBalancerFilterChain(this.index + 1, this);
            return loadBalancerFilter.filter(filterChain, request, serviceInstances);
        }
        return Flux.fromIterable(serviceInstances);
    }
}
