package cn.monkey.gateway.stream.traffic.config;

import cn.monkey.commons.data.Properties;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.stream.traffic")
public class TrafficShapingConfigurationProperties {

    static final String WRITE_LIMIT_KEY = "writeLimit";

    static final String DEFAULT_WRITE_LIMIT = String.valueOf(Long.MAX_VALUE);
    static final String READ_LIMIT_KEY = "readLimit";

    static final String DEFAULT_READ_LIMIT = String.valueOf(Long.MAX_VALUE);
    static final String WRITE_GLOBAL_LIMIT_KEY = "writeGlobalLimit";

    static final String DEFAULT_WRITE_GLOBAL_LIMIT = String.valueOf(Long.MAX_VALUE);
    static final String READ_GLOBAL_LIMIT_KEY = "readGlobalLimit";

    static final String DEFAULT_READ_GLOBAL_LIMIT = String.valueOf(Long.MAX_VALUE);
    static final String WRITE_CHANNEL_LIMIT_KEY = "writeChannelLimit";

    static final String DEFAULT_WRITE_CHANNEL_LIMIT = String.valueOf(Long.MAX_VALUE);
    static final String READ_CHANNEL_LIMIT_KEY = "readChannelLimit";

    static final String DEFAULT_READ_CHANNEL_LIMIT = String.valueOf(Long.MAX_VALUE);
    static final String CHECK_INTERVAL_KEY = "checkInterval";

    static final String DEFAULT_CHECK_INTERVAL = String.valueOf(AbstractTrafficShapingHandler.DEFAULT_CHECK_INTERVAL);
    static final String MAX_TIME_KEY = "maxTime";

    static final String DEFAULT_MAX_TIME = String.valueOf(AbstractTrafficShapingHandler.DEFAULT_MAX_TIME);

    private boolean enabled = false;
    private Properties props = new Properties();
    private String className = ChannelTrafficShapingHandler.class.getName();

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public String getClassName() {
        return className;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static abstract class Config {
        private long checkInterval;
        private long maxTime;

        private long writeLimit;
        private long readLimit;

        public long getCheckInterval() {
            return checkInterval;
        }

        public void setCheckInterval(long checkInterval) {
            this.checkInterval = checkInterval;
        }

        public long getMaxTime() {
            return maxTime;
        }

        public void setMaxTime(long maxTime) {
            this.maxTime = maxTime;
        }

        public long getWriteLimit() {
            return writeLimit;
        }

        public void setWriteLimit(long writeLimit) {
            this.writeLimit = writeLimit;
        }

        public long getReadLimit() {
            return readLimit;
        }

        public void setReadLimit(long readLimit) {
            this.readLimit = readLimit;
        }
    }

    public static class GlobalConfig extends Config {
    }

    public static class ChannelConfig extends Config {
    }

    public static class GroupChannelConfig extends Config {
        private long writeGlobalLimit;
        private long readGlobalLimit;
        private long writeChannelLimit;
        private long readChannelLimit;

        public long getWriteGlobalLimit() {
            return writeGlobalLimit;
        }

        public void setWriteGlobalLimit(long writeGlobalLimit) {
            this.writeGlobalLimit = writeGlobalLimit;
        }

        public long getReadGlobalLimit() {
            return readGlobalLimit;
        }

        public void setReadGlobalLimit(long readGlobalLimit) {
            this.readGlobalLimit = readGlobalLimit;
        }

        public long getWriteChannelLimit() {
            return writeChannelLimit;
        }

        public void setWriteChannelLimit(long writeChannelLimit) {
            this.writeChannelLimit = writeChannelLimit;
        }

        public long getReadChannelLimit() {
            return readChannelLimit;
        }

        public void setReadChannelLimit(long readChannelLimit) {
            this.readChannelLimit = readChannelLimit;
        }
    }

    long getLongValueOrDefault(String key, String defaultStr) {
        if (this.props == null) {
            return Long.parseLong(defaultStr);
        }
        return Long.parseLong((String) this.props.getOrDefault(key, defaultStr));
    }

    public GlobalConfig toGlobal() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setWriteLimit(getLongValueOrDefault(WRITE_LIMIT_KEY, DEFAULT_WRITE_LIMIT));
        globalConfig.setReadLimit(getLongValueOrDefault(READ_LIMIT_KEY, DEFAULT_READ_LIMIT));
        globalConfig.setCheckInterval(getLongValueOrDefault(CHECK_INTERVAL_KEY, DEFAULT_CHECK_INTERVAL));
        globalConfig.setMaxTime(getLongValueOrDefault(MAX_TIME_KEY, DEFAULT_MAX_TIME));
        return globalConfig;
    }

    public ChannelConfig toChannel() {
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setWriteLimit(getLongValueOrDefault(WRITE_LIMIT_KEY, DEFAULT_WRITE_LIMIT));
        channelConfig.setReadLimit(getLongValueOrDefault(READ_LIMIT_KEY, DEFAULT_READ_LIMIT));
        channelConfig.setCheckInterval(getLongValueOrDefault(CHECK_INTERVAL_KEY, DEFAULT_CHECK_INTERVAL));
        channelConfig.setMaxTime(getLongValueOrDefault(MAX_TIME_KEY, DEFAULT_MAX_TIME));
        return channelConfig;
    }

    public GroupChannelConfig toGroupChannel() {
        GroupChannelConfig groupChannelConfig = new GroupChannelConfig();
        groupChannelConfig.setWriteGlobalLimit(getLongValueOrDefault(WRITE_GLOBAL_LIMIT_KEY, DEFAULT_WRITE_GLOBAL_LIMIT));
        groupChannelConfig.setReadGlobalLimit(getLongValueOrDefault(READ_GLOBAL_LIMIT_KEY, DEFAULT_READ_GLOBAL_LIMIT));
        groupChannelConfig.setWriteChannelLimit(getLongValueOrDefault(WRITE_CHANNEL_LIMIT_KEY, DEFAULT_WRITE_CHANNEL_LIMIT));
        groupChannelConfig.setReadChannelLimit(getLongValueOrDefault(READ_CHANNEL_LIMIT_KEY, DEFAULT_READ_CHANNEL_LIMIT));
        groupChannelConfig.setCheckInterval(getLongValueOrDefault(CHECK_INTERVAL_KEY, DEFAULT_CHECK_INTERVAL));
        groupChannelConfig.setMaxTime(getLongValueOrDefault(MAX_TIME_KEY, DEFAULT_MAX_TIME));
        return groupChannelConfig;
    }
}
