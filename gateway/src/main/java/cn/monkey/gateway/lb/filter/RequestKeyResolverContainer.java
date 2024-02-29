package cn.monkey.gateway.lb.filter;

import cn.monkey.gateway.lb.config.Strategy;

public interface RequestKeyResolverContainer {
    RequestKeyResolver getRequestKeyResolver(String routeId);

    default RequestKeyResolver load(String strategy) {
        RequestKeyResolver requestKeyResolver;
        switch (strategy) {
            case Strategy.IP_HASH -> requestKeyResolver = new IPRequestKeyResolver();
            case Strategy.URL_HASH -> requestKeyResolver = new UrlRequestKeyResolver();
            default -> requestKeyResolver = NoopRequestKeyResolver.INSTANCE;
        }
        return requestKeyResolver;
    }
}
