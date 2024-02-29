package cn.monkey.data.pb;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;

import java.util.List;

public interface ChatCommandUtil {
    static Command.Pkg resultPkg(long cmdType,
                                 Any from,
                                 Any to,
                                 ByteString content,
                                 long timestamp) {
        return Command.Pkg
                .newBuilder()
                .setFrom(from)
                .setTo(to)
                .setCmdType(cmdType)
                .setContent(content)
                .setTimestamp(timestamp)
                .build();
    }

    static Any from(String serverId, Chat.User user) {
        Chat.From from = Chat.From.newBuilder()
                .setUser(user)
                .setServerId(serverId)
                .build();
        return Any.pack(from);
    }

    static Any to(String groupId, List<Chat.User> users) {
        Chat.To to = Chat.To.newBuilder()
                .setGroupId(groupId)
                .addAllUsers(users)
                .build();
        return Any.pack(to);
    }
}
