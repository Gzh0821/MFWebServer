package cn.monkey.commons.util;

import cn.monkey.commons.data.KVPair;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Map;

public interface ObjectPropsUtil {
    static Collection<KVPair<String, String>> build(HttpServletRequest httpServletRequest) {
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        return parameterMap.entrySet().stream().filter(e -> e.getKey().startsWith("props.") && e.getValue() != null && e.getValue().length > 0)
                .map(e -> KVPair.of(e.getKey(), e.getValue()[0]))
                .toList();
    }
}
