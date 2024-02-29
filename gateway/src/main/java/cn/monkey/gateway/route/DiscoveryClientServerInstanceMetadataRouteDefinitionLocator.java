package cn.monkey.gateway.route;

import cn.monkey.gateway.utils.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DiscoveryClientServerInstanceMetadataRouteDefinitionLocator extends DiscoveryClientRouteDefinitionLocator {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryClientServerInstanceMetadataRouteDefinitionLocator.class);

    protected final Map<String, RoutePredicateFactory<?>> routePredicateFactoryMap;
    protected final Map<String, GatewayFilterFactory<?>> gatewayFilterFactoryMap;

    public DiscoveryClientServerInstanceMetadataRouteDefinitionLocator(ReactiveDiscoveryClient discoveryClient,
                                                                       DiscoveryLocatorProperties properties,
                                                                       List<RoutePredicateFactory<?>> predicateFactories,
                                                                       List<GatewayFilterFactory<?>> gatewayFilterFactories) {
        super(discoveryClient, properties);
        this.routePredicateFactoryMap = predicateFactories.stream().collect(Collectors.toMap(RoutePredicateFactory::name, r -> r));
        this.gatewayFilterFactoryMap = gatewayFilterFactories.stream().collect(Collectors.toMap(GatewayFilterFactory::name, g -> g));
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return super.getRouteDefinitions().map(this::reBuildRouteDefinition);
    }

    protected RouteDefinition reBuildRouteDefinition(RouteDefinition origin) {
        Map<String, Object> metadata = origin.getMetadata();
        StringBuilder sb = new StringBuilder();
        metadata.forEach((key, value) -> sb.append(key).append("=").append(value).append("\n"));
        RouteDefinition definition;
        try {
            definition = ConfigurationUtils.load("metadata", "properties", sb.toString(), (binder -> binder.bind("", RouteDefinition.class)));
        } catch (Exception e) {
            log.error("configuration load error:\n", e);
            return origin;
        }
        if (definition == null) {
            return origin;
        }
        /*
         use client's definitions if they're exists, otherwise, use default gateway's components,
         */
        List<FilterDefinition> filters = definition.getFilters();
        if (!CollectionUtils.isEmpty(filters)) {
            List<FilterDefinition> originFilters = origin.getFilters();
            for (FilterDefinition fd : filters) {
                if (!originFilters.contains(fd)) {
                    originFilters.add(fd);
                }
            }
            originFilters.removeIf(filterDefinition -> {
                boolean b = !this.gatewayFilterFactoryMap.containsKey(filterDefinition.getName());
                if (!b && log.isDebugEnabled()) {
                    log.warn("can not find filter definition: {}", filterDefinition.getName());
                }
                return b;
            });
        }
        List<PredicateDefinition> predicates = definition.getPredicates();
        if (!CollectionUtils.isEmpty(predicates)) {
            List<PredicateDefinition> originPredicates = origin.getPredicates();
            for (PredicateDefinition pd : predicates) {
                if (!originPredicates.contains(pd)) {
                    originPredicates.add(pd);
                }
            }
            originPredicates.removeIf(predicateDefinition -> {
                boolean b = !this.routePredicateFactoryMap.containsKey(predicateDefinition.getName());
                if (!b && log.isDebugEnabled()) {
                    log.warn("can not find predicate definition: {}", predicateDefinition.getName());
                }
                return b;
            });
        }
        return origin;
    }
}

