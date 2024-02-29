package cn.monkey.gateway.lb.filter;

import cn.monkey.gateway.lb.config.LoadBalancerConfigProperties;
import cn.monkey.gateway.lb.config.LoadBalancerFilterDefinition;
import cn.monkey.gateway.lb.config.Strategy;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultLoadBalancerBeanContainer implements LoadBalancerBeanContainer, ApplicationContextAware,
        InitializingBean {
    protected Map<String, LoadBalancerFilterFactory> loadBalancerFilterFactoryMap;
    protected final Map<String, List<LoadBalancerFilter>> loadBalancerFilterMap;
    protected final Map<String, RequestKeyResolver> requestKeyResolverMap;
    protected LoadBalancerConfigProperties loadBalancerConfigProperties;
    protected RequestKeyResolver defaultRequestKeyResolver;
    protected List<LoadBalancerFilter> defaultLoadBalancerFilters;
    protected ApplicationContext applicationContext;

    public DefaultLoadBalancerBeanContainer() {
        this.loadBalancerFilterFactoryMap = Collections.emptyMap();
        this.loadBalancerFilterMap = new ConcurrentHashMap<>();
        this.requestKeyResolverMap = new ConcurrentHashMap<>();
    }

    @Override
    public List<LoadBalancerFilter> getSortedFilters(String routeId) {
        List<LoadBalancerFilter> loadBalancerFilters = this.loadBalancerFilterMap.computeIfAbsent(routeId, (key) -> {
            Optional<List<LoadBalancerFilter>> loadBalancerFiltersOptional = this.applyFromDefinition(key, loadBalancerDefinition -> {
                List<LoadBalancerFilterDefinition> filterDefinitions = loadBalancerDefinition.getFilters();
                if (CollectionUtils.isEmpty(filterDefinitions)) {
                    filterDefinitions = this.loadBalancerConfigProperties.getFilters();
                }
                if (CollectionUtils.isEmpty(filterDefinitions)) {
                    return null;
                }
                return filterDefinitions.stream().map(loadBalancerFilterDefinition -> {
                    String name = loadBalancerFilterDefinition.getName();
                    LoadBalancerFilterFactory loadBalancerFilterFactory = this.loadBalancerFilterFactoryMap.get(name);
                    if (loadBalancerFilterFactory == null) {
                        throw new IllegalArgumentException("can not find loadBalancerFilterFactory by name: " + name);
                    }
                    return loadBalancerFilterFactory.create(loadBalancerFilterDefinition);
                }).sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
            });
            return loadBalancerFiltersOptional.orElse(this.defaultLoadBalancerFilters);
        });
        return ImmutableList.copyOf(loadBalancerFilters);
    }

    @Override
    public RequestKeyResolver getRequestKeyResolver(String routeId) {
        return this.requestKeyResolverMap.computeIfAbsent(routeId, (key) -> {
            Optional<RequestKeyResolver> optional = this.applyFromDefinition(routeId, loaderBalancerDefinition -> {
                String strategy = loaderBalancerDefinition.getStrategy();
                if (Strings.isNullOrEmpty(strategy)) {
                    strategy = Strategy.DEFAULT;
                }
                return this.load(strategy);
            });
            return optional.orElse(this.defaultRequestKeyResolver);
        });
    }

    protected void initLoadBalancerConfigProperties() {
        String defaultStrategy = this.loadBalancerConfigProperties.getStrategy();
        this.defaultRequestKeyResolver = this.load(defaultStrategy);
        Map<String, LoadBalancerFilterFactory> loadBalancerFilterFactoryMap = this.applicationContext.getBeansOfType(LoadBalancerFilterFactory.class);
        List<LoadBalancerFilterDefinition> defaultFilterDefinitions = this.loadBalancerConfigProperties.getFilters();
        if (!CollectionUtils.isEmpty(loadBalancerFilterFactoryMap) && !CollectionUtils.isEmpty(defaultFilterDefinitions)) {
            this.loadBalancerFilterFactoryMap = loadBalancerFilterFactoryMap.values()
                    .stream().collect(Collectors.toMap(LoadBalancerFilterFactory::getName, l -> l));
            this.defaultLoadBalancerFilters = defaultFilterDefinitions.stream().map(loadBalancerFilterDefinition -> {
                String name = loadBalancerFilterDefinition.getName();
                LoadBalancerFilterFactory loadBalancerFilterFactory = this.loadBalancerFilterFactoryMap.get(name);
                if (loadBalancerFilterFactory == null) {
                    throw new IllegalArgumentException("can not find loadBalancerFilterFactory by name: " + name);
                }
                return loadBalancerFilterFactory.create(loadBalancerFilterDefinition);
            }).sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
        } else {
            this.defaultLoadBalancerFilters = Collections.emptyList();
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.initLoadBalancerConfigProperties();
    }

    @Override
    public LoadBalancerConfigProperties getLoadBalancerConfigProperties() {
        return this.loadBalancerConfigProperties;
    }

    @Override
    public void setLoadBalancerConfigProperties(LoadBalancerConfigProperties loadBalancerConfigProperties) {
        this.loadBalancerConfigProperties = loadBalancerConfigProperties;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
