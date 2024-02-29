package cn.monkey.gateway.lb.config;

import org.springframework.core.Ordered;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoadBalancerFilterDefinition implements Ordered {
    private String name;

    private int order = Ordered.LOWEST_PRECEDENCE;

    private Map<String, String> args = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
