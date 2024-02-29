package cn.monkey.socket.server.msg.data;

import java.io.Serializable;

/**
 * ack回执包
 * @param <T>
 */
public interface AckPackage<T extends Serializable> extends Serializable {
    T getPkg();
}
