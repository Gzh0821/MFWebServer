package cn.monkey.gateway.stream.auth.config;

import cn.monkey.gateway.components.dsl.Request;
import cn.monkey.gateway.components.dsl.RequestPredicateContainer;
import cn.monkey.gateway.components.dsl.RequestPredicateDefinition;
import cn.monkey.gateway.components.dsl.ServerHttpRequestPredicateFactory;
import cn.monkey.gateway.stream.auth.DefaultAuthRequestPredicateContainer;
import cn.monkey.gateway.stream.auth.rpc.RemoteAuthClient;
import cn.monkey.gateway.stream.auth.AuthClient;
import cn.monkey.gateway.stream.auth.AuthFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = AuthConfigProperties.CONFIGURATION_PROPERTIES_PREFIX + ".enabled", matchIfMissing = true)
public class AuthConfig {

    private static RequestPredicateDefinition initRequestPredicateDefinition() {
        RequestPredicateDefinition definition1 = new RequestPredicateDefinition();
        definition1.setPath("/user-center/signIn");
        definition1.setMethod(HttpMethod.POST.name());
        RequestPredicateDefinition definition2 = new RequestPredicateDefinition();
        definition2.setPath("/user-center/auth");
        definition2.setMethod(HttpMethod.POST.name());
        RequestPredicateDefinition definition = new RequestPredicateDefinition();

        RequestPredicateDefinition definition3 = new RequestPredicateDefinition();
        definition3.setPath("/telecom-system/**");
        definition3.setMethod(HttpMethod.POST.name());
        definition.setOr(List.of(definition1, definition2, definition3));
        definition.setNegate(true);
        return definition;
    }

    @Bean
    AuthConfigProperties authConfigProperties() {
        AuthConfigProperties authConfigProperties = new AuthConfigProperties();
        Request request = new Request();
        request.setPath("http://user-center/auth");
        authConfigProperties.setRequest(request);
        authConfigProperties.setFilter(initRequestPredicateDefinition());
        return authConfigProperties;
    }

    @Bean
    RequestPredicateContainer requestPredicateContainer(ServerHttpRequestPredicateFactory serverHttpRequestPredicateFactory,
                                                        AuthConfigProperties authConfigProperties) {
        return new DefaultAuthRequestPredicateContainer(serverHttpRequestPredicateFactory, authConfigProperties);
    }

    @Bean
    AuthClient remoteAuthClient(WebClient.Builder webClientBuilder,
                                AuthConfigProperties authConfigProperties) {
        return new RemoteAuthClient(webClientBuilder, authConfigProperties);
    }

    @Bean
    AuthFilter authFilter(AuthClient authClient,
                          @Qualifier("requestPredicateContainer")
                          RequestPredicateContainer requestPredicateContainer) {
        return new AuthFilter(authClient, requestPredicateContainer);
    }
}