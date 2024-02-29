package cn.monkey.gateway.trace.config;

import cn.monkey.gateway.trace.local.InMemoryTraceIdGenerator;
import cn.monkey.gateway.trace.TraceIdFilter;
import cn.monkey.gateway.trace.TraceIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "spring.cloud.trace.enabled", matchIfMissing = true)
public class TraceConfig {

    @Bean
    TraceIdGenerator traceIdGenerator() {
        return new InMemoryTraceIdGenerator();
    }

    @Bean
    TraceIdFilter traceIdFilter(TraceIdGenerator traceIdGenerator) {
        return new TraceIdFilter(traceIdGenerator);
    }
}
