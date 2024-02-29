package cn.monkey.gateway.stream.blacklist;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public record DefaultBlackEntity(String key) implements BlackEntity {

    public DefaultBlackEntity {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "[key] can not be null or empty");
    }
}
