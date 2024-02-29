package cn.monkey.gateway.stream.blacklist.sync;

import cn.monkey.spring.web.HttpHeaderConstants;

public class TokenKeyResolver extends HttpHeadersKeyResolver {

    public TokenKeyResolver() {
        super(HttpHeaderConstants.AUTHORIZATION_KEY);
    }
}
