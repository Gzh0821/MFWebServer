package cn.monkey.gateway.stream.blacklist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BlackListAutoConfiguration {

    @Bean
    BlackListConfigurationProperties blackListConfigurationProperties() {
        return new BlackListConfigurationProperties();
    }
}
