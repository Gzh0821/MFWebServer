package cn.monkey.commons.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSession implements Serializable {

    public static final String KEY = "user_session";

    private String uid;
    private String token;
    private String username;

    private String orgId;
    private String deptId;
}
