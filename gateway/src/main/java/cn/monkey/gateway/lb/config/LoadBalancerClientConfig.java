package cn.monkey.gateway.lb.config;

import cn.monkey.gateway.lb.filter.*;
import cn.monkey.gateway.lb.DelegateReactorServiceInstanceLoadBalancer;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancer;
import com.alibaba.cloud.nacos.util.InetIPv6Utils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.loadbalancer.support.SimpleObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
public class LoadBalancerClientConfig {

    private static RequestServiceInstanceListSupplier getRequestServiceInstanceListSupplier(String name,
                                                                                            LoadBalancerBeanContainer loadBalancerBeanContainer,
                                                                                            LoadBalancerClientFactory loadBalancerClientFactory) {
        Supplier<List<LoadBalancerFilter>> loadBalancerFiltersSupplier = () -> loadBalancerBeanContainer.getSortedFilters(name);
        Supplier<RequestKeyResolver> requestKeyResolverSupplier = () -> loadBalancerBeanContainer.getRequestKeyResolver(name);
        ObjectProvider<ServiceInstanceListSupplier> lazyProvider = loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class);
        LoadBalancerFilterChain loadBalancerFilterChain = new DefaultLoadBalancerFilterChain(loadBalancerFiltersSupplier);
        return new RequestServiceInstanceListSupplier(loadBalancerFilterChain,
                lazyProvider,
                requestKeyResolverSupplier,
                name);
    }

    @Bean
    @ConditionalOnMissingBean(ReactorServiceInstanceLoadBalancer.class)
    ReactorServiceInstanceLoadBalancer roundRobinLoadBalancer(Environment environment,
                                                              LoadBalancerBeanContainer loadBalancerBeanContainer,
                                                              LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        RequestServiceInstanceListSupplier requestServiceInstanceListSupplier = getRequestServiceInstanceListSupplier(name, loadBalancerBeanContainer, loadBalancerClientFactory);
        RoundRobinLoadBalancer roundRobinLoadBalancer = new RoundRobinLoadBalancer(
                new SimpleObjectProvider<>(requestServiceInstanceListSupplier),
                name);
        return new DelegateReactorServiceInstanceLoadBalancer(roundRobinLoadBalancer, requestServiceInstanceListSupplier);
    }

    @Bean
    @ConditionalOnBean(NacosDiscoveryProperties.class)
    ReactorServiceInstanceLoadBalancer nacosLoadBalancer(Environment environment,
                                                         LoadBalancerBeanContainer loadBalancerBeanContainer,
                                                         LoadBalancerClientFactory loadBalancerClientFactory,
                                                         NacosDiscoveryProperties nacosDiscoveryProperties,
                                                         InetIPv6Utils inetIPv6Utils) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        RequestServiceInstanceListSupplier requestServiceInstanceListSupplier = getRequestServiceInstanceListSupplier(name, loadBalancerBeanContainer, loadBalancerClientFactory);
        NacosLoadBalancer nacosLoadBalancer = new NacosLoadBalancer(
                new SimpleObjectProvider<>(requestServiceInstanceListSupplier),
                name, nacosDiscoveryProperties);
        // todo 是不是可以给nacos提一份pr？优化为构造方法注入 + InitializingBean
        try {
            Field declaredField = NacosLoadBalancer.class.getDeclaredField("inetIPv6Utils");
            declaredField.setAccessible(true);
            declaredField.set(nacosLoadBalancer, inetIPv6Utils);
            nacosLoadBalancer.init();
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException ignore) {
        }
        return new DelegateReactorServiceInstanceLoadBalancer(nacosLoadBalancer, requestServiceInstanceListSupplier);
    }
}
