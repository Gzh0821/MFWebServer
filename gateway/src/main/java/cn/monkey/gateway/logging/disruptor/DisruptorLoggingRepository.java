package cn.monkey.gateway.logging.disruptor;

import cn.monkey.gateway.logging.LoggingRepository;
import cn.monkey.gateway.logging.data.LoggingEntity;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DisruptorLoggingRepository implements LoggingRepository {

    private final Disruptor<RunnerWrapper> disruptor;

    private ThreadFactory threadFactory = Executors.defaultThreadFactory();

    private final Scheduler scheduler;

    private final LoggingRepository delegate;

    public DisruptorLoggingRepository(LoggingRepository delegate) {
        this.disruptor = new Disruptor<>(new LoggingEntityFactory(), 1024, threadFactory,
                ProducerType.SINGLE, new BlockingWaitStrategy());
        this.disruptor.handleEventsWith(new LoggingEventHandler());
        this.disruptor.start();
        this.delegate = delegate;
        this.scheduler = Schedulers.newSingle("disruptor scheduler");
    }

    @Override
    public Mono<Void> saveAndFlush(LoggingEntity entity) {
        RingBuffer<RunnerWrapper> ringBuffer = this.disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            RunnerWrapper runnerWrapper = ringBuffer.get(sequence);
            runnerWrapper.value = () -> this.delegate.saveAndFlush(entity)
                    .subscribeOn(this.scheduler)
                    .subscribe();
        } finally {
            ringBuffer.publish(sequence);
        }
        return Mono.empty();
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }
}
