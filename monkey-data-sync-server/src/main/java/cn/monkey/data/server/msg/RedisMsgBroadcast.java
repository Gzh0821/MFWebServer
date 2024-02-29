package cn.monkey.data.server.msg;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisMsgBroadcast implements MsgBroadcast {

    private static final Logger log = LoggerFactory.getLogger(RedisMsgBroadcast.class);

    private final RedissonClient redissonClient;

    public RedisMsgBroadcast(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void broadcast(String key, byte[] msg) {
        RBlockingQueue<byte[]> blockingQueue = this.redissonClient.getBlockingQueue(key);
        if (!blockingQueue.offer(msg)) {
            log.error("dropped msg: {}", msg);
        }
    }
}
