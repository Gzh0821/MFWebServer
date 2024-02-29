package cn.monkey.gateway.stream.ratelimit.config;

import org.springframework.cloud.gateway.support.HasRouteId;

public class RouteRateLimiterDefinition implements HasRouteId {
    private String routeId;
    private double permitsPerSecond = 30.0d;
    private long warmupPeriodMs = 10;
    private long expireTimeMs = 60 * 60 * 1000;

    public double getPermitsPerSecond() {
        return permitsPerSecond;
    }

    public void setPermitsPerSecond(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    public long getWarmupPeriodMs() {
        return warmupPeriodMs;
    }

    public void setWarmupPeriodMs(long warmupPeriodMs) {
        this.warmupPeriodMs = warmupPeriodMs;
    }

    public long getExpireTimeMs() {
        return expireTimeMs;
    }

    public void setExpireTimeMs(long expireTimeMs) {
        this.expireTimeMs = expireTimeMs;
    }

    @Override
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public String getRouteId() {
        return this.routeId;
    }
}
