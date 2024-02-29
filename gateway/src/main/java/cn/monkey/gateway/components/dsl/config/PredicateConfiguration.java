package cn.monkey.gateway.components.dsl.config;

import cn.monkey.gateway.components.dsl.DefaultServerHttpRequestPredicateFactory;
import cn.monkey.gateway.components.dsl.DefaultServerHttpResponsePredicateFactory;
import cn.monkey.gateway.components.dsl.ServerHttpRequestPredicateFactory;
import cn.monkey.gateway.components.dsl.ServerHttpResponsePredicateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PredicateConfiguration {
    @Bean
    ServerHttpResponsePredicateFactory serverHttpResponsePredicateFactory() {
        return new DefaultServerHttpResponsePredicateFactory();
    }

    @Bean
    ServerHttpRequestPredicateFactory serverHttpRequestPredicateFactory() {
        return new DefaultServerHttpRequestPredicateFactory();
    }
}
