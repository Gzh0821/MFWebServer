package cn.monkey.gateway.lb;

import cn.monkey.gateway.lb.filter.SelectedInstanceCallback;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import reactor.core.publisher.Mono;

public class DelegateReactorServiceInstanceLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    protected final ReactorServiceInstanceLoadBalancer delegate;

    protected final SelectedInstanceCallback selectedInstanceCallback;

    public DelegateReactorServiceInstanceLoadBalancer(ReactorServiceInstanceLoadBalancer delegate, SelectedInstanceCallback selectedInstanceCallback) {
        this.delegate = delegate;
        this.selectedInstanceCallback = selectedInstanceCallback;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        return this.delegate.choose(request)
                .doOnNext(serviceInstanceResponse -> {
                    if (serviceInstanceResponse.hasServer()) {
                        this.selectedInstanceCallback.selectedServiceInstance(request, serviceInstanceResponse.getServer());
                    }
                });
    }
}
