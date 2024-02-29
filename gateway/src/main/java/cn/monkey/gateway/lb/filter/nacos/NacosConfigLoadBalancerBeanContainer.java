package cn.monkey.gateway.lb.filter.nacos;

import cn.monkey.gateway.lb.config.LoadBalancerConfigProperties;
import cn.monkey.gateway.lb.config.LoadBalancerFilterDefinition;
import cn.monkey.gateway.lb.filter.LoadBalancerBeanContainer;
import cn.monkey.gateway.lb.filter.LoadBalancerFilter;
import cn.monkey.gateway.lb.filter.LoadBalancerFilterFactory;
import cn.monkey.gateway.lb.filter.RequestKeyResolver;
import cn.monkey.gateway.components.nacos.AbstractRefreshableNacosConfigSupport;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.*;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NacosConfigLoadBalancerBeanContainer
        extends AbstractRefreshableNacosConfigSupport<LoadBalancerConfigProperties>
        implements LoadBalancerBeanContainer, ApplicationContextAware {

    protected Map<String, LoadBalancerFilterFactory> loadBalancerFilterFactoryMap;
    protected volatile LoadBalancerConfigProperties loadBalancerConfigProperties;
    protected final Map<String, List<LoadBalancerFilter>> loadBalancerFilterMap;
    protected final Map<String, RequestKeyResolver> requestKeyResolverMap;
    protected volatile RequestKeyResolver defaultRequestKeyResolver;
    protected volatile List<LoadBalancerFilter> defaultLoadBalancerFilters;

    protected ApplicationContext applicationContext;

    public NacosConfigLoadBalancerBeanContainer(Environment environment,
                                                NacosConfigManager nacosConfigManager) {
        super(environment, nacosConfigManager);
        this.requestKeyResolverMap = new ConcurrentHashMap<>();
        this.loadBalancerFilterMap = new ConcurrentHashMap<>();
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
    public void accept(LoadBalancerConfigProperties loadBalancerConfigProperties) {
        this.setLoadBalancerConfigProperties(loadBalancerConfigProperties);
        this.initLoadBalancerConfigProperties();
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
                }).sorted(new AnnotationAwareOrderComparator()).toList();
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
                    return null;
                }
                return this.load(strategy);
            });
            return optional.orElse(this.defaultRequestKeyResolver);
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clear();
        super.afterPropertiesSet();
        this.initLoadBalancerConfigProperties();
    }

    private void clear() {
        this.loadBalancerFilterMap.clear();
        this.requestKeyResolverMap.clear();
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
