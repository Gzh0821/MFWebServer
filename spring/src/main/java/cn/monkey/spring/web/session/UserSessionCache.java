package cn.monkey.spring.web.session;

import cn.monkey.commons.data.UserSession;

public interface UserSessionCache {

    UserSession find(String token);

    void put(String key, UserSession userSession);

    void remove(String key);
}
