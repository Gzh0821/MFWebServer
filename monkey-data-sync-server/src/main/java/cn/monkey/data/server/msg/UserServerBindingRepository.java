package cn.monkey.data.server.msg;

import java.util.List;

public interface UserServerBindingRepository {
    List<String> select(String uid);

    void add(String serverId, String uid);

    void remove(String serverId, String uid);
}
