package cn.monkey.gateway.stream.blacklist.sync;

import com.google.common.base.Strings;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;

public class HttpHeadersKeyResolver extends HttpMessageKeyResolver {

    private final String[] headerKeys;

    public HttpHeadersKeyResolver(String... headerKeys) {
        this.headerKeys = headerKeys;
    }

    @Override
    protected String resolve0(HttpMessage httpMessage) {
        if (this.headerKeys == null || this.headerKeys.length == 0) {
            return null;
        }
        HttpHeaders headers = httpMessage.headers();
        if (headers == null) {
            return null;
        }
        for (String headerKey : headerKeys) {
            String s = headers.get(headerKey);
            if (!Strings.isNullOrEmpty(s)) {
                return s;
            }
        }
        return null;
    }
}
