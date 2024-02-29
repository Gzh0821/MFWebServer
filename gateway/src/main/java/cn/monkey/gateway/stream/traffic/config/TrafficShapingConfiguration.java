package cn.monkey.gateway.stream.traffic.config;

import cn.monkey.gateway.stream.traffic.DefaultTrafficShapingFactory;
import cn.monkey.gateway.stream.traffic.TrafficShapingCustomizer;
import cn.monkey.gateway.stream.traffic.TrafficShapingFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "spring.cloud.stream.traffic.enabled", matchIfMissing = true)
public class TrafficShapingConfiguration {

    @Bean
    TrafficShapingConfigurationProperties trafficShapingConfigurationProperties() {
        return new TrafficShapingConfigurationProperties();
    }

    @Bean
    TrafficShapingFactory trafficShapingFactory(TrafficShapingConfigurationProperties trafficShapingConfigurationProperties) {
        return new DefaultTrafficShapingFactory(trafficShapingConfigurationProperties);
    }

    @Bean
    TrafficShapingCustomizer trafficShapingCustomizer(TrafficShapingFactory trafficShapingFactory) {
        return new TrafficShapingCustomizer(trafficShapingFactory);
    }
}
