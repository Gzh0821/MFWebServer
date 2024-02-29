package cn.monkey.gateway.stream.blacklist.async;

import com.google.common.base.Preconditions;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ResponseStatusPredicate implements FailPredicate {

    private final HttpStatus[] failHttpStatus;

    public ResponseStatusPredicate(HttpStatus... httpStatuses) {
        Preconditions.checkArgument(httpStatuses != null, "httpStatuses can not be null");
        this.failHttpStatus = httpStatuses;
    }

    public ResponseStatusPredicate() {
        this(new HttpStatus[0]);
    }


    @Override
    public final Mono<Boolean> test(ServerWebExchange exchange) {
        HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
        if (statusCode == null) {
            return Mono.empty();
        }
        for (HttpStatus httpStatus : this.failHttpStatus) {
            if (httpStatus.value() == statusCode.value()) {
                return Mono.just(Boolean.TRUE);
            }
        }
        return Mono.empty();
    }

}
