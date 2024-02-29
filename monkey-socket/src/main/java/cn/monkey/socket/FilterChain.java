package cn.monkey.socket;

public interface FilterChain<Pkg> {
    void doFilter(Session session, Pkg pkg);
}
