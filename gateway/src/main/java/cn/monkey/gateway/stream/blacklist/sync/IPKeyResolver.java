package cn.monkey.gateway.stream.blacklist.sync;

import cn.monkey.gateway.utils.NetUtils;

public class IPKeyResolver extends HttpHeadersKeyResolver {

    public IPKeyResolver() {
        super(NetUtils.IP_HEADER_KEYS);
    }
}
