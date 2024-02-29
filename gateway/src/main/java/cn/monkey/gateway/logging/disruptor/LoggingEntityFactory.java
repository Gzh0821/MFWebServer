package cn.monkey.gateway.logging.disruptor;

import com.lmax.disruptor.EventFactory;

public class LoggingEntityFactory implements EventFactory<RunnerWrapper> {
    @Override
    public RunnerWrapper newInstance() {
        return new RunnerWrapper();
    }
}
