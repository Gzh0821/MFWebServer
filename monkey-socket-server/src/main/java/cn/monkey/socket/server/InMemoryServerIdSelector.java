package cn.monkey.socket.server;

import org.springframework.core.env.Environment;

public class InMemoryServerIdSelector implements ServerIdSelector {

    private final Environment environment;

    public static final String KEY = "monkey.server.id";

    public InMemoryServerIdSelector(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String select() {
        return environment.getProperty(KEY);
    }
}
