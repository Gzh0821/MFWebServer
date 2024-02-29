package cn.monkey.gateway.lb.filter;

import cn.monkey.gateway.lb.config.LoadBalancerFilterDefinition;
import cn.monkey.gateway.lb.utils.LbUtils;
import com.google.common.base.Strings;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public class RequestHeadersAndServiceInstanceMetadataLoadBalancerFilter implements LoadBalancerFilter, Ordered {

    public static final String COMPARE_VAL_KEY = "compareVal";

    private String compareVal;

    private final int order;

    public RequestHeadersAndServiceInstanceMetadataLoadBalancerFilter(LoadBalancerFilterDefinition loadBalancerFilterDefinition) {
        this.order = loadBalancerFilterDefinition.getOrder();
        Map<String, String> args = loadBalancerFilterDefinition.getArgs();
        this.setCompareVal(args.get(COMPARE_VAL_KEY));
    }

    protected HttpHeaders parseRequest(Request<?> request) {
        Object context = request.getContext();
        if (context instanceof RequestDataContext rdc) {
            RequestData clientRequest = rdc.getClientRequest();
            return clientRequest.getHeaders();
        }
        return null;
    }

    public void setCompareVal(String compareVal) {
        this.compareVal = compareVal;
    }

    protected Map<String, String> parseServiceInstance(ServiceInstance serviceInstance) {
        return serviceInstance.getMetadata();
    }

    @Override
    public Flux<ServiceInstance> filter(LoadBalancerFilterChain chain, Request<?> request, List<ServiceInstance> serviceInstances) {
        if (Strings.isNullOrEmpty(this.compareVal)) {
            return chain.doFilter(request, serviceInstances);
        }
        return chain.doFilter(request, serviceInstances.stream()
                .filter(serviceInstance -> LbUtils.compareHttpHeadersAndServiceInstanceMetadata(this.compareVal, this.parseRequest(request), this.parseServiceInstance(serviceInstance))).toList());
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
