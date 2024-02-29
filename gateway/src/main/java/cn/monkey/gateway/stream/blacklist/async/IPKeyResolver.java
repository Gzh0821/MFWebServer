package cn.monkey.gateway.stream.blacklist.async;

import cn.monkey.gateway.utils.NetUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class IPKeyResolver implements BlackKeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        return Mono.justOrEmpty(NetUtils.getIp(headers));
    }
}
