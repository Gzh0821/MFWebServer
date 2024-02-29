package cn.monkey.gateway.stream.ratelimit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RateLimiterConfiguration {

    @Bean
    RateLimiterConfigurationProperties rateLimiterConfigurationProperties() {
        return new RateLimiterConfigurationProperties();
    }

}
