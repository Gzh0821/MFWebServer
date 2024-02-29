package cn.monkey.gateway.components.dsl;

import java.util.List;
import java.util.Map;

/**
 * 查看 {@link RequestPredicateDefinition}
 */

public class ResponsePredicateDefinition {
    private Integer statusCode;

    private Map<String, String> headers;

    private boolean negate = Boolean.FALSE;

    private List<ResponsePredicateDefinition> or;
    private List<ResponsePredicateDefinition> and;


    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public boolean isNegate() {
        return negate;
    }

    public List<ResponsePredicateDefinition> getOr() {
        return or;
    }

    public void setOr(List<ResponsePredicateDefinition> or) {
        this.or = or;
    }

    public List<ResponsePredicateDefinition> getAnd() {
        return and;
    }

    public void setAnd(List<ResponsePredicateDefinition> and) {
        this.and = and;
    }
}
