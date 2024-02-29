package cn.monkey.gateway.lb.utils;

import cn.monkey.gateway.lb.filter.LoadBalancerFilterFactory;

public interface NameUtils {
    static String normalizeFilterFactoryName(Class<? extends LoadBalancerFilterFactory> clazz) {
        return clazz.getSimpleName().replace(LoadBalancerFilterFactory.class.getSimpleName(), "");
    }
}