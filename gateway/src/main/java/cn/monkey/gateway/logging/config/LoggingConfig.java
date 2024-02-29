package cn.monkey.gateway.logging.config;

import cn.monkey.gateway.components.dsl.*;
import cn.monkey.gateway.logging.LoggingFilter;
import cn.monkey.gateway.logging.LoggingRepository;
import cn.monkey.gateway.logging.disruptor.DisruptorLoggingRepository;
import cn.monkey.gateway.logging.rpc.RemoteSystemLoggingRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = LoggingConfigProperties.CONFIG_PROPERTIES_PREFIX + ".enabled", matchIfMissing = true)
public class LoggingConfig {

    static List<RequestPredicateDefinition> initRequestPredicateDefinitions() {
        RequestPredicateDefinition definition0 = new RequestPredicateDefinition();
        definition0.setMethod(HttpMethod.GET.name());
        definition0.setNegate(true);

        RequestPredicateDefinition definition4 = new RequestPredicateDefinition();
        definition4.setHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        RequestPredicateDefinition definition5 = new RequestPredicateDefinition();
        definition5.setHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE));
        RequestPredicateDefinition definition6 = new RequestPredicateDefinition();
        definition6.setOr(List.of(definition4, definition5));

        RequestPredicateDefinition definition1 = new RequestPredicateDefinition();
        definition1.setPath("/user-center/signIn");
        RequestPredicateDefinition definition2 = new RequestPredicateDefinition();
        definition2.setPath("/user-center/auth");
        RequestPredicateDefinition definition3 = new RequestPredicateDefinition();
        definition3.setPath("/telecom-system/**");
        RequestPredicateDefinition definition7 = new RequestPredicateDefinition();
        definition7.setOr(List.of(definition1, definition2, definition3));
        definition7.setNegate(true);
        return List.of(definition0, definition6, definition7);
    }

    static List<ResponsePredicateDefinition> initResponsePredicateDefinitions() {
        ResponsePredicateDefinition definition1 = new ResponsePredicateDefinition();
        definition1.setStatusCode(HttpStatus.OK.value());
        ResponsePredicateDefinition definition2 = new ResponsePredicateDefinition();
        definition2.setHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        ResponsePredicateDefinition definition3 = new ResponsePredicateDefinition();
        definition3.setHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE));
        ResponsePredicateDefinition definition4 = new ResponsePredicateDefinition();
        definition4.setOr(List.of(definition2, definition3));
        return List.of(definition1, definition4);
    }

    @Bean
    LoggingConfigProperties loggingConfigProperties() {
        LoggingConfigProperties loggingConfigProperties = new LoggingConfigProperties();
        loggingConfigProperties.setRequestPredicates(initRequestPredicateDefinitions());
        loggingConfigProperties.setResponsePredicates(initResponsePredicateDefinitions());
        Request request = new Request();
        request.setMethod("POST");
        request.setPath("http://telecom-system/logging");
        loggingConfigProperties.setLoggingRequest(request);
        return loggingConfigProperties;
    }

    @Bean
    DuplexServerWebPredicateContainer loggingServerWebPredicateContainer(ServerHttpRequestPredicateFactory serverHttpRequestPredicateFactory,
                                                                         ServerHttpResponsePredicateFactory serverHttpResponsePredicateFactory,
                                                                         LoggingConfigProperties loggingConfigProperties) {
        return new DefaultLoggingServerWebPredicateContainer(serverHttpRequestPredicateFactory, serverHttpResponsePredicateFactory, loggingConfigProperties);
    }

    @Bean
    LoggingRepository loggingRepository(LoggingConfigProperties loggingConfigProperties,
                                        WebClient.Builder webClientBuilder) {
        return new RemoteSystemLoggingRepository(loggingConfigProperties, webClientBuilder);
    }

    @Bean
    @Primary
    LoggingRepository disruptorLoggingRepository(LoggingRepository loggingRepository) {
        return new DisruptorLoggingRepository(loggingRepository);
    }


    @Bean
    LoggingFilter loggingFilter(List<HttpMessageReader<?>> messageReaders,
                                LoggingRepository loggingRepository,
                                @Qualifier("loggingServerWebPredicateContainer")
                                DuplexServerWebPredicateContainer serverWebPredicateContainer) {
        return new LoggingFilter(messageReaders, loggingRepository, serverWebPredicateContainer);
    }
}
