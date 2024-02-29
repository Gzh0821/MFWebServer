package cn.monkey.gateway.logging.disruptor;

import com.lmax.disruptor.EventHandler;
public class LoggingEventHandler implements EventHandler<RunnerWrapper> {

    public LoggingEventHandler() {
    }

    @Override
    public void onEvent(RunnerWrapper event, long sequence, boolean endOfBatch) throws Exception {
        Runnable value = event.value;
        value.run();
    }
}
