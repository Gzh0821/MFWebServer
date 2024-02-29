package cn.monkey.socket.server.msg;

import cn.monkey.socket.Session;

import java.util.List;

public interface UserSessionRepository {
    List<Session> selectByUid(String uid);
}
