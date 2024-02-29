package cn.monkey.gateway.logging.data;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;

public class Request implements Serializable {
    private HttpHeaders headers;
    private String url;
    private MultiValueMap<String, String> queryParams;
    private String method;
    private Byte[] payload;
    private Long timestamp;
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Byte[] getPayload() {
        return payload;
    }

    public void setPayload(Byte[] payload) {
        this.payload = payload;
    }

    public MultiValueMap<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(MultiValueMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
