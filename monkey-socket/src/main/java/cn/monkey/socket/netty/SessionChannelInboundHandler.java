package cn.monkey.socket.netty;

import cn.monkey.socket.Dispatcher;
import cn.monkey.socket.FilterChain;
import cn.monkey.socket.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.lang.Nullable;

public abstract class SessionChannelInboundHandler<I, Pkg> extends SimpleChannelInboundHandler<I> {

    protected final NettySessionManager nettySessionManager;

    protected final FilterChain<Pkg> filterChain;

    @Nullable
    protected final Dispatcher<Pkg> dispatcher;

    public SessionChannelInboundHandler(NettySessionManager nettySessionManager,
                                        FilterChain<Pkg> filterChain,
                                        @Nullable Dispatcher<Pkg> dispatcher) {
        super();
        this.nettySessionManager = nettySessionManager;
        this.filterChain = filterChain;
        this.dispatcher = dispatcher;
    }
    protected abstract Pkg decode(I msg);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
        Pkg pkg = this.decode(msg);
        Session session = this.nettySessionManager.findOrCreate(ctx);
        try {
            this.filterChain.doFilter(session, pkg);
        } catch (Exception e) {
            session.close();
            return;
        }
        if (this.dispatcher != null) {
            this.dispatcher.accept(session, pkg);
        }
    }
}