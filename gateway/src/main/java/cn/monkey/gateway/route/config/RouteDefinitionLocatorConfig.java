package cn.monkey.gateway.route.config;

import cn.monkey.gateway.route.DiscoveryClientServerInstanceMetadataRouteDefinitionLocator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.discovery.GatewayDiscoveryClientAutoConfiguration;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Configuration
@AutoConfigureAfter(GatewayDiscoveryClientAutoConfiguration.ReactiveDiscoveryClientRouteDefinitionLocatorConfiguration.class)
public class RouteDefinitionLocatorConfig {

    @Bean
    @ConditionalOnMissingBean(DiscoveryClientRouteDefinitionLocator.class)
    public DiscoveryClientServerInstanceMetadataRouteDefinitionLocator discoveryClientServerInstanceMetadataRouteDefinitionLocator(
            List<RoutePredicateFactory<?>> predicateFactories, List<GatewayFilterFactory<?>> gatewayFilterFactories,
            ReactiveDiscoveryClient discoveryClient, DiscoveryLocatorProperties properties) {
        List<FilterDefinition> filters = properties.getFilters();
        if (CollectionUtils.isEmpty(filters)) {
            List<FilterDefinition> filterDefinitions = GatewayDiscoveryClientAutoConfiguration.initFilters();
            properties.setFilters(filterDefinitions);
        } else {
            filters.addAll(GatewayDiscoveryClientAutoConfiguration.initFilters());
            properties.setFilters(filters);
        }
        List<PredicateDefinition> predicates = properties.getPredicates();
        if (CollectionUtils.isEmpty(predicates)) {
            properties.setPredicates(GatewayDiscoveryClientAutoConfiguration.initPredicates());
        } else {
            predicates.addAll(GatewayDiscoveryClientAutoConfiguration.initPredicates());
            properties.setPredicates(predicates);
        }
        return new DiscoveryClientServerInstanceMetadataRouteDefinitionLocator(discoveryClient, properties, predicateFactories, gatewayFilterFactories);
    }
}
