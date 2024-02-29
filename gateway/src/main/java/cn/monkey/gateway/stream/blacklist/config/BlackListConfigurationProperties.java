package cn.monkey.gateway.stream.blacklist.config;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;

import java.util.List;

@ConfigurationProperties(prefix = "spring.cloud.stream.blacklist")
public class BlackListConfigurationProperties {

    private Cache cache = new Cache();

    private Key key = new Key();

    private Fail fail = new Fail();

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Fail getFail() {
        return fail;
    }

    public void setFail(Fail fail) {
        this.fail = fail;
    }

    public static class Fail {
        private List<Integer> failHttpRespCode = Lists.newArrayList(HttpStatus.PAYLOAD_TOO_LARGE.value(),
                HttpStatus.TOO_MANY_REQUESTS.value(),HttpStatus.NOT_ACCEPTABLE.value());
        private int maxCount = Integer.MAX_VALUE;

        private long countExpireTimeMs = 24 * 60 * 60 * 1000;

        public List<Integer> getFailHttpRespCode() {
            return failHttpRespCode;
        }

        public void setFailHttpRespCode(List<Integer> failHttpRespCode) {
            this.failHttpRespCode = failHttpRespCode;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        public long getCountExpireTimeMs() {
            return countExpireTimeMs;
        }

        public void setCountExpireTimeMs(long countExpireTimeMs) {
            this.countExpireTimeMs = countExpireTimeMs;
        }
    }

    public static class Key {
        private String[] headers;

        private List<String> blackKeys;

        public String[] getHeaders() {
            return headers;
        }

        public void setHeaders(String[] headers) {
            this.headers = headers;
        }

        public List<String> getBlackKeys() {
            return blackKeys;
        }

        public void setBlackKeys(List<String> blackKeys) {
            this.blackKeys = blackKeys;
        }
    }

    public static class Cache {
        private long autoRemoveTimeMs = 0;

        private String name = "cache";

        public long getAutoRemoveTimeMs() {
            return autoRemoveTimeMs;
        }

        public void setAutoRemoveTimeMs(long autoRemoveTimeMs) {
            this.autoRemoveTimeMs = autoRemoveTimeMs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
