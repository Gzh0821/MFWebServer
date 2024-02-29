package cn.monkey.socket.server.util;

import cn.monkey.data.pb.Chat;
import cn.monkey.data.pb.Command;
import cn.monkey.socket.server.msg.data.User;

public interface ProtobufMsgUtil {
    static Chat.User user(User user) {
        return Chat.User.newBuilder()
                .setId(user.getId())
                .setName(user.getUsername())
                .build();
    }
}
