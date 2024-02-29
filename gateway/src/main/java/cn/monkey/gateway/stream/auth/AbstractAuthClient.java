package cn.monkey.gateway.stream.auth;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

import java.util.*;

public abstract class AbstractAuthClient implements AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractAuthClient.class);

    protected boolean isJsonContentType(@Nullable MediaType mediaType) {
        return mediaType == null || Objects.equals(MediaType.APPLICATION_JSON, mediaType);
    }


    protected void logErrorPath(ServerWebExchange exchange, String msg) {
        String path = exchange.getRequest().getURI().getPath();
        log.error("path: {} filter error: \n{}", path, msg);
    }
}
