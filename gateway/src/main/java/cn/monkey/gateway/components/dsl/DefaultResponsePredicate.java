package cn.monkey.gateway.components.dsl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DefaultResponsePredicate implements ResponsePredicate {

    private final ResponsePredicateDefinition definition;

    public DefaultResponsePredicate(ResponsePredicateDefinition predicateDefinition) {
        this.definition = predicateDefinition;
    }

    protected boolean testStatusCode(HttpStatusCode httpStatusCode) {
        Integer statusCode = definition.getStatusCode();
        if (statusCode == null) {
            return true;
        }
        if (statusCode == httpStatusCode.value()) {
            return true;
        }
        return false;
    }

    protected boolean testResponseHeaders(HttpHeaders httpHeaders) {
        Map<String, String> headersMap = this.definition.getHeaders();
        if (CollectionUtils.isEmpty(headersMap)) {
            return true;
        }
        for (Map.Entry<String, String> e : headersMap.entrySet()) {
            String key = e.getKey();
            List<String> strings = httpHeaders.get(key);
            if (CollectionUtils.isEmpty(strings)) {
                return false;
            }
            if (!strings.contains(e.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean test(ServerHttpResponse serverHttpResponse) {
        if (!this.testStatusCode(serverHttpResponse.getStatusCode())) {
            return false;
        }
        if (!this.testResponseHeaders(serverHttpResponse.getHeaders())) {
            return false;
        }
        return true;
    }
}
