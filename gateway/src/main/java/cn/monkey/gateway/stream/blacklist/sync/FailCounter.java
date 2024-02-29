package cn.monkey.gateway.stream.blacklist.sync;

public interface FailCounter {
    long getCount(String key);

    default long incrementAndGet(String key) {
        return this.incrementAndGet(key, 1L);
    }

    long incrementAndGet(String key, long delta);
}
