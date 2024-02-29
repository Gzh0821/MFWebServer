package cn.monkey.data.server.msg;

public interface MsgBroadcast {
    void broadcast(String key, byte[] msg);
}
