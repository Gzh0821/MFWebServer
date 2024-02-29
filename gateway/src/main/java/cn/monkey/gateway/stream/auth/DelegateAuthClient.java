package cn.monkey.gateway.stream.auth;

public abstract class DelegateAuthClient extends AbstractAuthClient {

    protected final AuthClient delegate;

    public DelegateAuthClient(AuthClient delegate) {
        this.delegate = delegate;
    }

    public AuthClient getDelegate() {
        return delegate;
    }
}
