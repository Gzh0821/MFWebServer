package cn.monkey.socket;

import java.util.function.BiConsumer;

public interface Dispatcher<Pkg> extends BiConsumer<Session, Pkg> {
    void accept(Session session, Pkg pkg);
}
