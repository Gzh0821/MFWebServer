package cn.monkey.gateway.stream.blacklist.sync;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ResponseStatusPredicate implements FailPredicate {

    private final HttpResponseStatus[] failHttpStatus;

    public ResponseStatusPredicate(HttpResponseStatus... httpStatuses) {
        Preconditions.checkArgument(httpStatuses != null, "httpStatuses can not be null");
        this.failHttpStatus = httpStatuses;
    }

    public ResponseStatusPredicate() {
        this(new HttpResponseStatus[0]);
    }


    @Override
    public boolean test(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpResponse httpMessage) {
            HttpResponseStatus status = httpMessage.status();
            for (HttpResponseStatus responseStatus : this.failHttpStatus) {
                if (responseStatus == status) {
                    return true;
                }
            }
        }
        return false;
    }
}
