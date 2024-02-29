package cn.monkey.spring.web.session;

import cn.monkey.commons.data.UserSession;
import reactor.core.publisher.Mono;

public interface ReactiveUserSessionCache {

    Mono<UserSession> find(String token);

    Mono<Void> put(String key, UserSession userSession);

    Mono<Void> remove(String key);
}
