package cn.monkey.state.scheduler.strategy;


public class YieldWaitingStrategy implements WaitingStrategy {
    @Override
    public void await() {
        Thread.yield();
    }
}
