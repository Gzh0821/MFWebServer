package cn.monkey.data.server.state;

import cn.monkey.data.pb.Chat;
import cn.monkey.data.pb.Command;
import cn.monkey.data.server.msg.InMemoryUserServerBindingRepository;
import cn.monkey.data.server.msg.UserServerBindingRepository;
import cn.monkey.data.server.msg.MsgBroadcast;
import cn.monkey.state.core.StateContext;

import java.util.ArrayList;
import java.util.List;

public class MessageContext implements StateContext {
    final UserServerBindingRepository userServerBindingRepository;

    final MsgBroadcast msgBroadcast;

    final List<Chat.User> currentUser;

    final List<byte[]> msg = new ArrayList<>();

    final List<Command.Pkg> newMsg = new ArrayList<>();

    public MessageContext(MsgBroadcast msgBroadcast) {
        this.userServerBindingRepository = new InMemoryUserServerBindingRepository();
        this.msgBroadcast = msgBroadcast;
        this.currentUser = new ArrayList<>();
    }
}
