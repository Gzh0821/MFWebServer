package cn.monkey.gateway.lb.filter;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RequestServiceInstanceListSupplier implements ServiceInstanceListSupplier, SelectedInstanceCallback {

    private final String serviceId;

    private final ObjectProvider<ServiceInstanceListSupplier> objectProvider;


    private final LoadBalancerFilterChain loadBalancerFilterChain;

    private final Cache<String, ServiceInstance> serviceInstanceCache;

    private final Supplier<RequestKeyResolver> keyResolverSupplier;

    public RequestServiceInstanceListSupplier(LoadBalancerFilterChain loadBalancerFilterChain,
                                              ObjectProvider<ServiceInstanceListSupplier> objectProvider,
                                              Supplier<RequestKeyResolver> keyResolverSupplier,
                                              String serviceId) {
        this.serviceId = serviceId;
        this.objectProvider = objectProvider;
        this.loadBalancerFilterChain = loadBalancerFilterChain;
        this.keyResolverSupplier = keyResolverSupplier;
        this.serviceInstanceCache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public String getServiceId() {
        return this.serviceId;
    }

    @Override
    public Flux<List<ServiceInstance>> get(Request request) {
        return this.objectProvider.getIfAvailable(NoopServiceInstanceListSupplier::new)
                .get(request).flatMap(serviceInstances -> this.filter(request, serviceInstances));
    }

    protected Flux<List<ServiceInstance>> filter(Request<?> request, List<ServiceInstance> serviceInstances) {
        return this.loadBalancerFilterChain.doFilter(request, serviceInstances)
                .collectList()
                .map(serviceInstanceList -> {
                    String resolve = this.keyResolverSupplier.get().resolve(request);
                    if (Strings.isNullOrEmpty(resolve)) {
                        return serviceInstanceList;
                    }
                    ServiceInstance instance = this.serviceInstanceCache.getIfPresent(resolve);
                    if (instance == null) {
                        return serviceInstanceList;
                    }
                    if (serviceInstanceList.contains(instance)) {
                        return Collections.singletonList(instance);
                    }
                    return serviceInstanceList;
                })
                .flux();
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return this.objectProvider.getIfAvailable(NoopServiceInstanceListSupplier::new).get();
    }

    @Override
    public void selectedServiceInstance(Request<?> request, ServiceInstance serviceInstance) {
        RequestKeyResolver requestKeyResolver = this.keyResolverSupplier.get();
        String resolve = requestKeyResolver.resolve(request);
        if (!Strings.isNullOrEmpty(resolve)) {
            this.serviceInstanceCache.asMap().put(resolve, serviceInstance);
        }
    }
}
