package cn.monkey.gateway.trace;

import cn.monkey.gateway.utils.NetUtils;
import cn.monkey.spring.web.HttpHeaderConstants;
import com.google.common.base.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements GlobalFilter {

    private final TraceIdGenerator traceIdGenerator;

    public TraceIdFilter(TraceIdGenerator traceIdGenerator) {
        this.traceIdGenerator = traceIdGenerator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String val = NetUtils.getValFromHeaders(exchange.getRequest().getHeaders(), HttpHeaderConstants.TRACE_ID_KEY);
        if (Strings.isNullOrEmpty(val)) {
            return this.traceIdGenerator.generate()
                    .flatMap(s -> {
                        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                        String routeId = Optional.ofNullable(route).map(Route::getId).orElse("no_route");
                        ServerHttpRequest request = exchange.getRequest();
                        ServerHttpRequest httpRequest = request.mutate().header(HttpHeaderConstants.TRACE_ID_KEY, routeId + "_" + s).build();
                        return chain.filter(exchange.mutate().request(httpRequest).build());
                    })
                    .switchIfEmpty(chain.filter(exchange));
        } else {
            return chain.filter(exchange);
        }
    }
}
