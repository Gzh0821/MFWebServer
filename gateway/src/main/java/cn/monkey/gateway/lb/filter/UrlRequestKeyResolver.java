package cn.monkey.gateway.lb.filter;

import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;

public class UrlRequestKeyResolver implements RequestKeyResolver {
    @Override
    public String resolve(Request<?> request) throws IllegalArgumentException {
        Object context = request.getContext();
        if (context instanceof RequestDataContext rdc) {
            RequestData clientRequest = rdc.getClientRequest();
            return clientRequest.getUrl().toString();
        }
        return null;
    }
}
