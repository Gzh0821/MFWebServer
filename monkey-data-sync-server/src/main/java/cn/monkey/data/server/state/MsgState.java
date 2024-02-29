package cn.monkey.data.server.state;

import cn.monkey.commons.data.KVPair;
import cn.monkey.data.ChatCmdType;
import cn.monkey.data.pb.Chat;
import cn.monkey.data.pb.ChatCommandUtil;
import cn.monkey.data.pb.Command;
import cn.monkey.data.server.msg.UserServerBindingRepository;
import cn.monkey.state.core.AbstractState;
import cn.monkey.state.core.StateInfo;
import cn.monkey.state.util.Timer;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MsgState extends AbstractState {
    public MsgState(String code, MsgGroup stateGroup) {
        super(code, stateGroup);
    }

    @Override
    public MsgGroup getStateGroup() {
        return (MsgGroup) super.getStateGroup();
    }

    protected final void broadCast(Command.PkgGroup pg) {
        MessageContext stateContext = getStateGroup().getStateContext();
    }

    @Override
    public void update(Timer timer, StateInfo stateInfo) throws Exception {
        MessageContext stateContext = this.getStateGroup().getStateContext();
        List<Command.Pkg> newMsg = stateContext.newMsg;
        if (CollectionUtils.isEmpty(newMsg)) {
            return;
        }
        this.routeMsg(newMsg);
    }

    protected void routeMsg(List<Command.Pkg> msgList) throws Exception {
        MessageContext stateContext = this.getStateGroup().getStateContext();
        List<Chat.User> currentUser = stateContext.currentUser;
        if (CollectionUtils.isEmpty(currentUser)) {
            return;
        }
        Map<String, List<Command.Pkg>> serverPkg = new HashMap<>();

        for (Command.Pkg pkg : msgList) {
            List<KVPair<String, Command.Pkg>> kvPairs = this.analysisPkgContent(pkg);
        }
    }

    private Command.Pkg enterGroup(Command.Pkg pkg,) throws InvalidProtocolBufferException {
        final MessageContext stateContext = this.getStateGroup().getStateContext();
        Chat.Msg msg = Chat.Msg.parseFrom(pkg.getContent());
        Chat.User user = msg.getFrom();
        stateContext.currentUser.add(user);
        return ChatCommandUtil.resultPkg(ChatCmdType.ENTER_GROUP.toOther(), pkg.getFrom(), pkg.getTo(), pkg.getContent(), pkg.getTimestamp());
    }

    protected List<KVPair<String, Command.Pkg>> analysisPkgContent(Command.Pkg pkg) throws InvalidProtocolBufferException {
        final MessageContext stateContext = this.getStateGroup().getStateContext();
        final UserServerBindingRepository userServerBindingRepository = stateContext.userServerBindingRepository;
        long cmdType = pkg.getCmdType();
        Chat.Msg msg = Chat.Msg.parseFrom(pkg.getContent());
        Chat.User user = msg.getFrom();
        Command.Pkg resultPkg;
        List<String> targetIds = stateContext.currentUser.stream().map(Chat.User::getId).toList();
        if (ChatCmdType.ENTER_GROUP.index() == cmdType) {
            stateContext.currentUser.add(user);
            resultPkg = ChatCommandUtil.resultPkg(ChatCmdType.ENTER_GROUP.toOther(), targetIds, pkg.getContent(), pkg.getTimestamp());
        } else if (ChatCmdType.EXIST_GROUP.index() == cmdType) {
            stateContext.currentUser.remove(user);
            resultPkg = ChatCommandUtil.resultPkg(ChatCmdType.EXIST_GROUP.toOther(), targetIds, pkg.getContent(), pkg.getTimestamp());
        } else if (ChatCmdType.OFFLINE.index() == cmdType) {
            resultPkg = ChatCommandUtil.resultPkg(ChatCmdType.OFFLINE.toOther(), targetIds, pkg.getContent(), pkg.getTimestamp());
        } else {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(targetIds)) {
            return Collections.emptyList();
        }
        return targetIds.stream().map(userServerBindingRepository::select)
                .map(List::stream)
                .reduce(Stream::concat)
                .map(Stream::toList)
                .orElse(Collections.emptyList())
                .stream()
                .map(s -> KVPair.of(s, resultPkg))
                .toList();
    }

    @Override
    public String finish(Timer timer) throws Exception {
        return null;
    }
}
