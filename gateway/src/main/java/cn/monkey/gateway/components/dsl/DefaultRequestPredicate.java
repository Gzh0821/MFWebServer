package cn.monkey.gateway.components.dsl;

import cn.monkey.gateway.utils.NetUtils;
import com.google.common.base.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public class DefaultRequestPredicate implements RequestPredicate {

    private final RequestPredicateDefinition definition;

    public DefaultRequestPredicate(RequestPredicateDefinition definition) {
        this.definition = definition;
    }

    protected boolean testMethod(HttpMethod httpMethod) {
        String method = definition.getMethod();
        return Strings.isNullOrEmpty(method) || method.equals(httpMethod.name());
    }

    protected boolean testQueryParams(MultiValueMap<String, String> queryParams) {
        Map<String, String> queryMap = definition.getQueryParams();
        if (CollectionUtils.isEmpty(queryMap)) {
            return true;
        }
        for (Map.Entry<String, String> e : queryMap.entrySet()) {
            String key = e.getKey();
            List<String> strings = queryParams.get(key);
            if (CollectionUtils.isEmpty(strings)) {
                return false;
            }
            if (!strings.contains(e.getValue())) {
                return false;
            }
        }
        return true;
    }

    protected boolean testPath(String path) {
        String definitionPath = this.definition.getPath();
        if (Strings.isNullOrEmpty(definitionPath)) {
            return true;
        }
        return definitionPath.contains("/**") ? NetUtils.matchPath(definitionPath, path) : definitionPath.equals(path);
    }

    protected boolean testHeaders(HttpHeaders headers) {
        Map<String, String> headersMap = this.definition.getHeaders();
        if (CollectionUtils.isEmpty(headersMap)) {
            return true;
        }
        for (Map.Entry<String, String> e : headersMap.entrySet()) {
            String key = e.getKey();
            List<String> strings = headers.get(key);
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
    public boolean test(ServerHttpRequest serverHttpRequest) {
        if (!testMethod(serverHttpRequest.getMethod())) {
            return false;
        }
        if (!testQueryParams(serverHttpRequest.getQueryParams())) {
            return false;
        }
        if (!testHeaders(serverHttpRequest.getHeaders())) {
            return false;
        }
        if (!this.testPath(serverHttpRequest.getPath().pathWithinApplication().value())) {
            return false;
        }
        return true;
    }
}
