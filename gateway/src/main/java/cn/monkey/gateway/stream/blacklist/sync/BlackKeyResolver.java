package cn.monkey.gateway.stream.blacklist.sync;

public interface BlackKeyResolver {
    default String resolve(Object msg) {
        return null;
    }
}
