package cn.monkey.gateway.lb.filter;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import reactor.core.publisher.Mono;

public interface SelectedInstanceCallback {

    void selectedServiceInstance(Request<?> request, ServiceInstance serviceInstance);
}
