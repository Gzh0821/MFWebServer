package cn.monkey.data.pb;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;

public interface CommandUtil {
    static Command.Pkg pkg(long cmdType,
                           Any from,
                           Any to,
                           ByteString content,
                           long timestamp) {
        Command.Pkg.Builder builder = Command.Pkg.newBuilder();
        if (from != null) {
            builder.setFrom(from);
        }
                .setCmdType(cmdType)
                .setFrom(from)
                .setTo(to)
                .setContent(content)
                .setTimestamp(timestamp)
                .build();
    }
}
