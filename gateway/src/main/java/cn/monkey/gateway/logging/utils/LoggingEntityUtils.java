package cn.monkey.gateway.logging.utils;

import cn.monkey.gateway.logging.data.Request;
import cn.monkey.gateway.logging.data.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

public interface LoggingEntityUtils {
    static Request request(ServerWebExchange serverWebExchange, byte[] bytes) {
        ServerHttpRequest serverHttpRequest = serverWebExchange.getRequest();
        MultiValueMap<String, String> queryParams = serverHttpRequest.getQueryParams();
        HttpHeaders headers = serverHttpRequest.getHeaders();
        String path = serverHttpRequest.getURI().getPath();
        Request request = new Request();
        request.setHeaders(headers);
        request.setMethod(serverHttpRequest.getMethod().name());
        request.setQueryParams(queryParams);
        request.setTimestamp(System.currentTimeMillis());
        request.setUrl(path);
        if (bytes != null) {
            request.setPayload(copyByte(bytes));
        }
        return request;
    }


    static Byte[] copyByte(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Byte[] bs = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bs[i] = bytes[i];
        }
        return bs;
    }

    static Response response(ServerWebExchange serverWebExchange, byte[] bytes) {
        ServerHttpResponse serverHttpResponse = serverWebExchange.getResponse();
        HttpHeaders headers = serverHttpResponse.getHeaders();
        HttpStatusCode statusCode = serverHttpResponse.getStatusCode();
        Response response = new Response();
        response.setHeaders(headers);
        response.setTimestamp(System.currentTimeMillis());
        response.setStatusCode(statusCode == null ? null : statusCode.value());
        if (bytes != null) {
            response.setPayload(copyByte(bytes));
        }
        return response;
    }
}
