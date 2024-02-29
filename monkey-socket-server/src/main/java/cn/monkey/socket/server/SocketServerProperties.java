package cn.monkey.socket.server;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.monkey.server")
public class SocketServerProperties {
    private int port = 8080;
    private String protocol = "/monkey";
    private int bossSize = 2;
    private int workerSize = 4;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    public int getBossSize() {
        return bossSize;
    }

    public void setBossSize(int bossSize) {
        this.bossSize = bossSize;
    }

    public int getWorkerSize() {
        return workerSize;
    }

    public void setWorkerSize(int workerSize) {
        this.workerSize = workerSize;
    }
}
