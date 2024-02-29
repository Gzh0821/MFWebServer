package cn.monkey.gateway.lb.filter;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoadBalancerFilterChain {
    Flux<ServiceInstance> doFilter(Request<?> request, List<ServiceInstance> serviceInstanceFlux);
}
