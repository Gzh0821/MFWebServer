package cn.monkey.socket.netty;

import cn.monkey.socket.Filter;
import cn.monkey.socket.FilterChain;
import cn.monkey.socket.Session;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class DefaultFilterChain<Pkg> implements FilterChain<Pkg> {

    private final List<Filter<Pkg>> filters;

    public DefaultFilterChain(List<Filter<Pkg>> filters) {
        this.filters = filters;
    }

    @Override
    public void doFilter(Session session, Pkg pkg) {
        if (CollectionUtils.isEmpty(filters)) {
            return;
        }
        for (Filter<Pkg> filter : filters) {
            filter.doFilter(this, session, pkg);
        }
    }
}
