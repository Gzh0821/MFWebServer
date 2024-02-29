package cn.monkey.gateway.lb.config;

public interface Strategy {
    String IP_HASH = "ipHash";

    String URL_HASH = "urlHash";

    String DEFAULT = "default";
}
