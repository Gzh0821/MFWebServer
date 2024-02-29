package cn.monkey.gateway.stream.blacklist.sync;

import com.google.common.base.Preconditions;

import java.util.Optional;

class CompositeBlackKeyResolver implements BlackKeyResolver {
    private final BlackKeyResolver previous;

    private final BlackKeyResolver next;

    CompositeBlackKeyResolver(BlackKeyResolver previous, BlackKeyResolver next) {
        Preconditions.checkNotNull(previous);
        Preconditions.checkNotNull(next);
        this.previous = previous;
        this.next = next;
    }

    @Override
    public String resolve(Object msg) {
        return Optional.ofNullable(previous.resolve(msg)).orElse(next.resolve(msg));
    }
}
