package cn.monkey.gateway.logging.rpc;

import cn.monkey.gateway.components.dsl.Request;
import cn.monkey.gateway.logging.LoggingRepository;
import cn.monkey.gateway.logging.config.LoggingConfigProperties;
import cn.monkey.gateway.logging.data.LoggingEntity;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoteSystemLoggingRepository implements LoggingRepository {

    private static final Logger log = LoggerFactory.getLogger(RemoteSystemLoggingRepository.class);
    private final LoggingConfigProperties loggingConfigProperties;
    private final WebClient.Builder webClientBuilder;

    public RemoteSystemLoggingRepository(LoggingConfigProperties loggingConfigProperties, WebClient.Builder webClientBuilder) {
        this.loggingConfigProperties = loggingConfigProperties;
        this.webClientBuilder = webClientBuilder;
    }

    private static HttpHeaders rebuildHeaders(HttpHeaders headers) {
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, List<String>> e : entries) {
            String key = e.getKey();
            if (HttpHeaders.CONTENT_TYPE.equals(key)) {
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            } else {
                httpHeaders.put(key, e.getValue());
            }
        }
        if (CollectionUtils.isEmpty(httpHeaders.get(HttpHeaders.CONTENT_TYPE))) {
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }
        return httpHeaders;
    }

    @Override
    public Mono<Void> saveAndFlush(LoggingEntity entity) {
        try {
            Request loggingRequest = this.loggingConfigProperties.getLoggingRequest();
            String method = loggingRequest.getMethod();
            HttpMethod httpMethod = Strings.isNullOrEmpty(method) ? HttpMethod.GET : HttpMethod.valueOf(method);
            final WebClient webClient = this.webClientBuilder.build();
            return webClient.method(httpMethod)
                    .uri(loggingRequest.getPath())
                    .bodyValue(entity)
                    .headers(headers -> {
                        headers.addAll(rebuildHeaders(entity.getRequest().getHeaders()));
                    })
                    .exchangeToMono(clientResponse -> {
                        HttpStatusCode httpStatusCode = clientResponse.statusCode();
                        if (log.isDebugEnabled()) {
                            log.debug("remote logging response status: {}", httpStatusCode);
                        }
                        return Mono.empty();
                    })
                    .then();
        } catch (Exception e) {
            log.error("saveAndFlush error:\n", e);
            return Mono.empty();
        }
    }
}
