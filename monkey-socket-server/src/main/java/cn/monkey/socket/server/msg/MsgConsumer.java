package cn.monkey.socket.server.msg;

import cn.monkey.data.pb.Command;

import java.util.function.Consumer;

public interface MsgConsumer extends Consumer<Command.PkgGroup> {

}
