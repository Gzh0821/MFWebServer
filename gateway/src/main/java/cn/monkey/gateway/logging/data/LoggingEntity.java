package cn.monkey.gateway.logging.data;

import cn.monkey.commons.data.KVPair;

import java.io.Serializable;

public class LoggingEntity implements Serializable {
    private String traceId;
    private KVPair<String, String> operator;
    private Request request;
    private Response response;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public KVPair<String, String> getOperator() {
        return operator;
    }

    public void setOperator(KVPair<String, String> operator) {
        this.operator = operator;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
