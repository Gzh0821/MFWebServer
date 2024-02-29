package cn.monkey.data.server.msg;

import cn.monkey.data.pb.Command;

import java.util.function.Consumer;

public interface MsgConsumer extends Consumer<Command.Pkg> {

}
