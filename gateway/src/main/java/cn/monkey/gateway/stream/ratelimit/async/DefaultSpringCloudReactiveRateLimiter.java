package cn.monkey.gateway.stream.ratelimit.async;

import cn.monkey.gateway.stream.ratelimit.config.RateLimiterConfigurationProperties;
import cn.monkey.gateway.stream.ratelimit.config.RouteRateLimiterDefinition;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * @see org.springframework.cloud.gateway.filter.ratelimit.RateLimiter
 * @see org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory
 */
public class DefaultSpringCloudReactiveRateLimiter extends AbstractRateLimiter<RouteRateLimiterDefinition> {

    static final Map<String, String> EMPTY_HEADERS = Collections.emptyMap();

    public static final String CONFIGURATION_PROPERTY_NAME = "rate-limiter";

    private final ReactiveRateLimiterContainer reactiveRateLimiterContainer;

    private final RateLimiterConfigurationProperties rateLimiterConfigurationProperties;

    public DefaultSpringCloudReactiveRateLimiter(ConfigurationService configurationService, ReactiveRateLimiterContainer reactiveRateLimiterContainer,
                                                 RateLimiterConfigurationProperties rateLimiterConfigurationProperties) {
        super(RouteRateLimiterDefinition.class, CONFIGURATION_PROPERTY_NAME, configurationService);
        this.reactiveRateLimiterContainer = reactiveRateLimiterContainer;
        this.rateLimiterConfigurationProperties = rateLimiterConfigurationProperties;
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        RouteRateLimiterDefinition definition = this.getConfig().getOrDefault(routeId, rateLimiterConfigurationProperties);
        return this.reactiveRateLimiterContainer.findOrCreate(routeId, definition)
                .flatMap(ReactiveRateLimiter::tryAcquire)
                .map(flag -> new Response(flag, EMPTY_HEADERS));
    }
}
