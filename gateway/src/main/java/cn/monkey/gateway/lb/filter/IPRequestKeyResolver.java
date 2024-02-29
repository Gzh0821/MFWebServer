package cn.monkey.gateway.lb.filter;

import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;


import static cn.monkey.gateway.utils.NetUtils.getIp;

public class IPRequestKeyResolver implements RequestKeyResolver {
    @Override
    public String resolve(Request<?> request) throws IllegalArgumentException {
        Object context = request.getContext();
        if (context instanceof RequestDataContext rdc) {
            RequestData clientRequest = rdc.getClientRequest();
            return getIp(clientRequest.getHeaders());
        }
        return null;
    }
}