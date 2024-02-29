package cn.monkey.commons.bean;


import cn.monkey.commons.data.Authorization;

public interface AuthorizationKeyManager extends KeyManager<Authorization> {
    String AUTHORIZATION_KEY = "authorization";

    boolean isValidKey(String key);
}
