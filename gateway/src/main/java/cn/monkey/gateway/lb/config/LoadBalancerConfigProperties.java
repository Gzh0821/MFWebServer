package cn.monkey.gateway.lb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collection;


@ConfigurationProperties(prefix = "spring.cloud.lb.dynamic")
public class LoadBalancerConfigProperties extends LoadBalancerDefinition {
    private Collection<LoadBalancerDefinition> clients;

    public Collection<LoadBalancerDefinition> getClients() {
        return clients;
    }

    public void setClients(Collection<LoadBalancerDefinition> clients) {
        this.clients = clients;
    }
}
