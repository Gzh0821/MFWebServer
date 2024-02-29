package cn.monkey.gateway.logging.data;

import org.springframework.http.HttpHeaders;

import java.io.Serializable;

public class Response implements Serializable {
    private HttpHeaders headers;
    private Integer statusCode;
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

    public Byte[] getPayload() {
        return payload;
    }

    public void setPayload(Byte[] payload) {
        this.payload = payload;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
