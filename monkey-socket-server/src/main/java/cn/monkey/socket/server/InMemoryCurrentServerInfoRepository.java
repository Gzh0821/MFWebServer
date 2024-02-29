package cn.monkey.socket.server;

public class InMemoryCurrentServerInfoRepository implements CurrentServerInfoRepository {
    @Override
    public String selectServerId() {
        return "1";
    }
}
