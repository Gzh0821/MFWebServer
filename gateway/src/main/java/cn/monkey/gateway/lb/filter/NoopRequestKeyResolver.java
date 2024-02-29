package cn.monkey.gateway.lb.filter;

public class NoopRequestKeyResolver implements RequestKeyResolver {
    public static final RequestKeyResolver INSTANCE = new NoopRequestKeyResolver();
}
