package cn.monkey.state.scheduler;


import cn.monkey.state.scheduler.strategy.WaitingStrategy;

import java.util.function.Supplier;

public interface StateGroupSchedulerFactory extends SchedulerFactory {
    StateGroupScheduler create(long id);

    void setWaitingStrategySupplier(Supplier<WaitingStrategy> strategySupplier);

}
