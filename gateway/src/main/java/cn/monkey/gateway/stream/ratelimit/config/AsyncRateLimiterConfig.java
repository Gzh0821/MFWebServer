package cn.monkey.gateway.stream.ratelimit.config;

import cn.monkey.gateway.components.dsl.RequestPredicateDefinition;
import cn.monkey.gateway.components.dsl.ServerHttpRequestPredicateFactory;
import cn.monkey.gateway.stream.ratelimit.async.*;
import cn.monkey.gateway.stream.ratelimit.async.local.GuavaReactiveRateLimiterFactory;
import cn.monkey.gateway.stream.ratelimit.async.redisson.RedissonReactiveRateLimiterFactory;
import com.google.common.base.Strings;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@ConditionalOnProperty(value = AsyncRateLimiterConfig.CONFIGURATION_PROPERTIES_PREFIX + ".enabled")
//@Configuration
public class AsyncRateLimiterConfig {

    static final String CONFIGURATION_PROPERTIES_PREFIX = "spring.cloud.stream.rate-limit.async";

    @Bean
    RateLimiter<RouteRateLimiterDefinition> rateLimiter(ConfigurationService configurationService,
                                                        ReactiveRateLimiterContainer reactiveRateLimiterContainer,
                                                        RateLimiterConfigurationProperties rateLimiterConfigurationProperties) {
        return new DefaultSpringCloudReactiveRateLimiter(configurationService, reactiveRateLimiterContainer, rateLimiterConfigurationProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    ReactiveRateLimiterFactory reactiveRateLimiterFactory() {
        return new GuavaReactiveRateLimiterFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    ReactiveRateLimiterContainer reactiveRateLimiterContainer(ReactiveRateLimiterFactory reactiveRateLimiterFactory) {
        return new DefaultReactiveRateLimiterContainer(reactiveRateLimiterFactory);
    }

    @Bean
    ReactiveKeyResolver requestMatchKeyResolver(ServerHttpRequestPredicateFactory requestPredicateFactory,
                                                RateLimiterConfigurationProperties configurationProperties) {
        Collection<RequestPredicateDefinition> requestPredicates = configurationProperties.getRequestPredicates();
        if (CollectionUtils.isEmpty(requestPredicates)) {
            return new NOOPKeyResolver();
        }
        Predicate<ServerHttpRequest> serverHttpRequestPredicate = requestPredicates.stream().map(requestPredicateFactory)
                .reduce(Predicate::and).get();
        return new RequestMatchKeyResolver(serverHttpRequestPredicate);
    }

    @Bean(RequestRateLimiterGatewayFilterFactory.KEY_RESOLVER_KEY)
    @Primary
    KeyResolver defaultKeyResolver(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String property = environment.getProperty(CONFIGURATION_PROPERTIES_PREFIX + ".keyResolver", "token,ip");
        Map<String, ReactiveKeyResolver> keyResolverMap = context.getBeansOfType(ReactiveKeyResolver.class);

        CompositeRateLimiterKeyResolverBuilder builder = CompositeRateLimiterKeyResolverBuilder.builder();
        if (!CollectionUtils.isEmpty(keyResolverMap)) {
            List<ReactiveKeyResolver> list = keyResolverMap.values().stream().sorted(AnnotationAwareOrderComparator.INSTANCE)
                    .toList();
            list.forEach(builder::with);
        }

        if (Strings.isNullOrEmpty(property)) {
            return builder.withIp().build();
        }
        String[] split = property.split(",");
        for (String s : split) {
            if ("ip".equals(s)) {
                builder.withIp();
                continue;
            }
            if ("token".equals(s)) {
                builder.withToken();
            }
        }
        return builder.build();
    }

    @Configuration
    @ConditionalOnBean(RedissonClient.class)
    static class RedissonRateLimiterConfiguration {
        @Bean
        ReactiveRateLimiterFactory reactiveRateLimiterFactory(RedissonClient redissonClient) {
            return new RedissonReactiveRateLimiterFactory(redissonClient);
        }
    }
}
