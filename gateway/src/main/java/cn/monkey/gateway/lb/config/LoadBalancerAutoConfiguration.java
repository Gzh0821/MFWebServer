/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.monkey.gateway.lb.config;

import cn.monkey.gateway.lb.filter.*;
import cn.monkey.gateway.lb.filter.nacos.NacosConfigLoadBalancerBeanContainer;
import cn.monkey.gateway.lb.utils.NameUtils;
import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosConfigManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@ConditionalOnNacosDiscoveryEnabled
@LoadBalancerClients(defaultConfiguration = LoadBalancerClientConfig.class)
public class LoadBalancerAutoConfiguration {

    @Bean
    WebClientCustomizer loadBalancererWebClientCustomizer(LoadBalancedExchangeFilterFunction loadBalancedExchangeFilterFunction) {
        return webClientBuilder -> webClientBuilder.filter(loadBalancedExchangeFilterFunction);
    }

    public static List<LoadBalancerFilterDefinition> initFilterDefinition() {
        List<LoadBalancerFilterDefinition> list = new ArrayList<>(2);
        LoadBalancerFilterDefinition grayEnvLoadBalancerFilterDefinition = new LoadBalancerFilterDefinition();
        grayEnvLoadBalancerFilterDefinition.setName(NameUtils.normalizeFilterFactoryName(RequestHeadersAndServiceInstanceMetadataLoadBalancerFilterFactory.class));
        grayEnvLoadBalancerFilterDefinition.setArgs(Map.of(RequestHeadersAndServiceInstanceMetadataLoadBalancerFilter.COMPARE_VAL_KEY, "version"));
        list.add(grayEnvLoadBalancerFilterDefinition);
        LoadBalancerFilterDefinition zoneLoaderBalancerFilterDefinition = new LoadBalancerFilterDefinition();
        zoneLoaderBalancerFilterDefinition.setName(NameUtils.normalizeFilterFactoryName(RequestHeadersAndServiceInstanceMetadataLoadBalancerFilterFactory.class));
        zoneLoaderBalancerFilterDefinition.setArgs(Map.of(RequestHeadersAndServiceInstanceMetadataLoadBalancerFilter.COMPARE_VAL_KEY, "zone"));
        list.add(zoneLoaderBalancerFilterDefinition);
        return list;
    }

    @Bean
    LoadBalancerConfigProperties loadBalancerConfigProperties() {
        LoadBalancerConfigProperties loadBalancerConfigProperties = new LoadBalancerConfigProperties();
        loadBalancerConfigProperties.setFilters(initFilterDefinition());
        return loadBalancerConfigProperties;
    }


    @Bean
    LoadBalancerFilterFactory dynamicLoadBalancerFilterFactory() {
        return new RequestHeadersAndServiceInstanceMetadataLoadBalancerFilterFactory();
    }

    @Bean
    @ConditionalOnMissingBean(LoadBalancerBeanContainer.class)
    LoadBalancerBeanContainer loadBalancerBeanContainer(LoadBalancerConfigProperties loadBalancerConfigProperties) {
        DefaultLoadBalancerBeanContainer defaultLoadBalancerBeanContainer = new DefaultLoadBalancerBeanContainer();
        defaultLoadBalancerBeanContainer.setLoadBalancerConfigProperties(loadBalancerConfigProperties);
        return defaultLoadBalancerBeanContainer;
    }

    @Configuration
    @ConditionalOnBean(NacosConfigManager.class)
    @ConditionalOnDiscoveryEnabled
    static class NacosLoadBalancerConfig {
        @Bean
        LoadBalancerBeanContainer loadBalancerBeanContainer(ConfigurableApplicationContext applicationContext,
                                                            NacosConfigManager nacosConfigManager,
                                                            LoadBalancerConfigProperties loadBalancerConfigProperties) {
            NacosConfigLoadBalancerBeanContainer nacosConfigLoadBalancerBeanContainer = new NacosConfigLoadBalancerBeanContainer(applicationContext.getEnvironment(), nacosConfigManager);
            nacosConfigLoadBalancerBeanContainer.setLoadBalancerConfigProperties(loadBalancerConfigProperties);
            return nacosConfigLoadBalancerBeanContainer;
        }
    }
}