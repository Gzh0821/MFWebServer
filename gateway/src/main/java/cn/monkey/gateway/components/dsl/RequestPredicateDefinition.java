package cn.monkey.gateway.components.dsl;

import java.util.List;
import java.util.Map;

/**
 * 借鉴了mongoScripts的设计思路
 * <pre>
 *  {@code
 *   // 不匹配get请求
 *    mongo script: "method": {$not:"GET"} =>
 *    definition: {method:"GET",negate: true}
 *    }
 * </pre>
 * <pre>
 *  {@code
 *   // 不匹配 "/user-center/signIn" 或者"telecom-system/**"
 *   mongo script: {$not: {$or: [{"path":"/user-center/signIn"},{"path":"telecom-system/**"}}}]}}    =>
 *   definition: {or: [{"path":"/user-center/signIn"},{"path":"telecom-system/**"}], negate: true}
 *   }
 * </pre>
 */
public class RequestPredicateDefinition {
    private String method;
    private String path;
    private Map<String, String> queryParams;

    private Map<String, String> headers;

    private boolean negate = Boolean.FALSE;

    private List<RequestPredicateDefinition> or;

    private List<RequestPredicateDefinition> and;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public List<RequestPredicateDefinition> getOr() {
        return or;
    }

    public void setOr(List<RequestPredicateDefinition> or) {
        this.or = or;
    }

    public List<RequestPredicateDefinition> getAnd() {
        return and;
    }

    public void setAnd(List<RequestPredicateDefinition> and) {
        this.and = and;
    }
}
