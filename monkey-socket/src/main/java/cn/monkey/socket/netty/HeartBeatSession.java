package cn.monkey.socket.netty;

import cn.monkey.commons.util.Timer;
import io.netty.channel.Channel;

public class HeartBeatSession extends NettySession {

    protected final Timer timer;

    private long maxIdleTimeMs = 5000;
    private volatile long lastOperateTime;

    public HeartBeatSession(Channel channel,
                            Timer timer) {
        super(channel);
        this.timer = timer;
        this.lastOperateTime = timer.getCurrentTimeMs();
    }

    public HeartBeatSession(HeartBeatSession session){
        super(session);
        this.timer =session.timer;
        this.maxIdleTimeMs = session.maxIdleTimeMs;
        this.lastOperateTime = session.lastOperateTime;
    }

    public void setMaxIdleTimeMs(long maxIdleTimeMs) {
        this.maxIdleTimeMs = maxIdleTimeMs;
    }

    public void flushLastOperateTime() {
        this.lastOperateTime = this.timer.getCurrentTimeMs();
    }

    @Override
    public boolean isActive() {
        return super.isActive() && this.timer.getCurrentTimeMs() - this.lastOperateTime <= this.maxIdleTimeMs;
    }
}
