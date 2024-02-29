package cn.monkey.gateway.logging;

import cn.monkey.gateway.logging.data.LoggingEntity;
import reactor.core.publisher.Mono;

public interface LoggingRepository {
    Mono<Void> saveAndFlush(LoggingEntity entity);
}