package cn.monkey.gateway.logging.config;

import cn.monkey.gateway.components.dsl.Request;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(LoggingConfigProperties.CONFIG_PROPERTIES_PREFIX)
public class LoggingConfigProperties extends LoggingDefinition {

    public static final String CONFIG_PROPERTIES_PREFIX = "spring.cloud.gateway.system.logging";
    private Request loggingRequest = new Request();
    private List<LoggingDefinition> clients;

    public List<LoggingDefinition> getClients() {
        return clients;
    }

    public Request getLoggingRequest() {
        return loggingRequest;
    }

    public void setLoggingRequest(Request loggingRequest) {
        this.loggingRequest = loggingRequest;
    }

    public void setClients(List<LoggingDefinition> clients) {
        this.clients = clients;
    }
}
