package cn.monkey.socket.netty;

import io.netty.bootstrap.ServerBootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CompositeServerBootStrapCustomizer implements ServerBootstrapCustomizer {
    private final Function<ServerBootstrap, ServerBootstrap> func;

    public CompositeServerBootStrapCustomizer(List<? extends Function<ServerBootstrap, ServerBootstrap>> customizers) {
        List<Function<ServerBootstrap, ServerBootstrap>> functionList = new ArrayList<>(customizers);
        this.func = functionList.stream().reduce(Function::andThen)
                .orElse(serverBootstrap -> serverBootstrap);
    }

    @Override
    public ServerBootstrap apply(ServerBootstrap serverBootstrap) {
        return func.apply(serverBootstrap);
    }
}
