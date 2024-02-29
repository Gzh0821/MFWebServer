package cn.monkey.spring.web;


import cn.monkey.commons.data.UserSession;

public interface UserSessionContext {
    UserSession get();
    void put(UserSession userSession);

    void remove();
}
