package cn.monkey.gateway.lb.filter;

import cn.monkey.gateway.lb.config.LoadBalancerConfigProperties;
import cn.monkey.gateway.lb.config.LoadBalancerDefinition;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface LoadBalancerBeanContainer extends LoadBalancerFilterContainer, RequestKeyResolverContainer {

    LoadBalancerConfigProperties getLoadBalancerConfigProperties();

    void setLoadBalancerConfigProperties(LoadBalancerConfigProperties loadBalancerConfigProperties);

    default <T> Optional<T> applyFromDefinition(String routId, Function<LoadBalancerDefinition, T> func) {
        LoadBalancerConfigProperties loadBalancerConfigProperties = this.getLoadBalancerConfigProperties();
        if (loadBalancerConfigProperties == null) {
            throw new NullPointerException();
        }
        Collection<LoadBalancerDefinition> definitions = loadBalancerConfigProperties.getClients();
        if (CollectionUtils.isEmpty(definitions)) {
            return Optional.empty();
        }
        Map<String, LoadBalancerDefinition> loaderBalancerDefinitionMap = definitions.stream().collect(Collectors.toMap(LoadBalancerDefinition::getRouteId, l -> l));
        LoadBalancerDefinition loaderBalancerDefinition = loaderBalancerDefinitionMap.get(routId);
        if (loaderBalancerDefinition == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(func.apply(loaderBalancerDefinition));
    }
}
