package cn.monkey.gateway.stream.blacklist.sync;

import io.netty.handler.codec.http.HttpMessage;

public abstract class HttpMessageKeyResolver implements BlackKeyResolver {
    @Override
    public String resolve(Object msg) {
        if (msg instanceof HttpMessage httpMessage) {
            return this.resolve0(httpMessage);
        }
        return null;
    }

    protected abstract String resolve0(HttpMessage httpMessage);
}
