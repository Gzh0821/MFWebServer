package cn.monkey.gateway.lb.filter;

import org.springframework.cloud.client.loadbalancer.Request;

public interface RequestKeyResolver {

    default String resolve(Request<?> request) {
        return null;
    }
}
