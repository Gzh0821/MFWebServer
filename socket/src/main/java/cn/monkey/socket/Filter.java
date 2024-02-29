package cn.monkey.socket;

public interface Filter<Pkg> {
    void doFilter(FilterChain<Pkg> chain, Session session, Pkg pkg);
}