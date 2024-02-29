package cn.monkey.state.scheduler;


import cn.monkey.state.scheduler.strategy.WaitingStrategy;

import java.util.function.Supplier;

public interface EventPublishSchedulerFactory extends SchedulerFactory {
    EventPublishScheduler create(long id);


    void setWaitingStrategy(Supplier<WaitingStrategy> waitingStrategy);
}
