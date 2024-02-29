package cn.monkey.gateway.utils;

import cn.monkey.gateway.stream.auth.data.Resource;
import cn.monkey.spring.web.HttpHeaderConstants;
import com.google.common.base.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NetUtils {
    String[] IP_HEADER_KEYS = {"X-Real-IP", "X-Forwarded-For"};

    /**
     * add nginx config
     * <p>
     * proxy_set_header X-Real-IP $remote_addr;<br></br>
     * proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
     * </P>
     */
    static String getIp(HttpHeaders headers) {
        for (String key : IP_HEADER_KEYS) {
            String val = getValFromHeaders(headers, key);
            if (!Strings.isNullOrEmpty(val)) {
                return val;
            }
        }
        return null;
    }

    static String getValFromHeaders(HttpHeaders headers, String key) {
        List<String> strings = headers.get(key);
        if (!CollectionUtils.isEmpty(strings)) {
            return strings.get(0);
        }
        return null;
    }

    AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    static boolean matchPath(String source, String target) {
        return source.contains("/**") ? ANT_PATH_MATCHER.match(source, target) : source.equals(target);
    }

    static Object buildBody(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();
        Resource resource = new Resource();
        resource.setPlatformId(NetUtils.getValFromHeaders(request.getHeaders(), HttpHeaderConstants.PLATFORM_ID_KEY));
        resource.setUrl(path);
        resource.setRequestMethod(method.name());
        return resource;
    }

    static HttpHeaders buildJsonContentHeaders(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, List<String>> e : entries) {
            String key = e.getKey();
            if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(key)) {
                httpHeaders.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
            } else {
                httpHeaders.put(key, e.getValue());
            }
        }
        return httpHeaders;
    }
}
