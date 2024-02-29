package cn.monkey.gateway.lb.utils;

import com.google.common.base.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public interface LbUtils {
    static boolean compareHttpHeadersAndServiceInstanceMetadata(String key, HttpHeaders headers, Map<String, String> metadata) {
        if (Strings.isNullOrEmpty(key)) {
            return true;
        }
        String s = metadata.get(key);
        if (Strings.isNullOrEmpty(s)) {
            return true;
        }
        if (CollectionUtils.isEmpty(headers)) {
            return false;
        }
        List<String> strings = headers.get(key);
        if (CollectionUtils.isEmpty(strings)) {
            return false;
        }
        return strings.contains(s);
    }
}
