package cn.monkey.socket.server;

public interface ServerIdSelector {
    /**
     * @return current server id
     */
    String select();
}
