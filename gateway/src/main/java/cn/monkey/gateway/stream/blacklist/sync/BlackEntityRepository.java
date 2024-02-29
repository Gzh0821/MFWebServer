package cn.monkey.gateway.stream.blacklist.sync;

import cn.monkey.gateway.stream.blacklist.BlackEntity;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

public interface BlackEntityRepository {
    default List<BlackEntity> selectAll() {
        return Collections.emptyList();
    }

    default boolean containsKey(String key) {
        return false;
    }

    @Nullable
    default BlackEntity add(BlackEntity blackListEntity) {
        return null;
    }

    /**
     * @return removed entity
     */
    default BlackEntity remove(BlackEntity blackListEntity) {
        return null;
    }
}
