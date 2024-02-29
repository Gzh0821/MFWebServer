package cn.monkey.state.scheduler;

import cn.monkey.commons.bean.Countable;
import cn.monkey.commons.bean.Refreshable;
import cn.monkey.state.core.StateGroup;
import cn.monkey.state.core.StateGroupPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleSchedulerManager implements SchedulerManager, Countable, Refreshable, Closeable {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final StateGroupPool stateGroupPool;

    protected final StateGroupSchedulerFactory stateGroupSchedulerFactory;

    protected final EventPublishSchedulerFactory eventPublishSchedulerFactory;

    protected final EventPublishScheduler[] eventPublishSchedulers;

    protected final SchedulerManagerConfig schedulerManagerConfig;

    private volatile boolean isRunning;

    protected volatile ConcurrentHashMap<Long, StateGroupScheduler> stateGroupSchedulerMap;

    private static final VarHandle STATE_GROUP_SCHEDULER_MAP_HANDLE;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            STATE_GROUP_SCHEDULER_MAP_HANDLE = lookup.findVarHandle(SimpleSchedulerManager.class, "stateGroupSchedulerMap", ConcurrentHashMap.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final AtomicLong stateGroupSchedulerIdCounter = new AtomicLong(0);


    public SimpleSchedulerManager(StateGroupPool stateGroupPool,
                                  StateGroupSchedulerFactory stateGroupSchedulerFactory,
                                  EventPublishSchedulerFactory eventPublishSchedulerFactory,
                                  SchedulerManagerConfig managerConfig) {
        this.stateGroupPool = stateGroupPool;
        this.stateGroupSchedulerFactory = stateGroupSchedulerFactory;
        this.eventPublishSchedulerFactory = eventPublishSchedulerFactory;
        this.schedulerManagerConfig = managerConfig;
        this.eventPublishSchedulers = this.initEventPublishSchedulers();
        this.stateGroupSchedulerMap = this.createStateGroupSchedulerMap();
        this.isRunning = true;
    }

    protected ConcurrentHashMap<Long, StateGroupScheduler> createStateGroupSchedulerMap() {
        return new ConcurrentHashMap<>();
    }

    protected final EventPublishScheduler[] initEventPublishSchedulers() {
        int eventPublisherSchedulerSize = this.schedulerManagerConfig.getEventPublisherSchedulerSize();
        if (eventPublisherSchedulerSize != 1 && Integer.bitCount(eventPublisherSchedulerSize) != 1) {
            throw new IllegalArgumentException("bufferSize must be a power of 2 or equals to 1");
        }
        EventPublishScheduler[] eventPublishSchedulers = new EventPublishScheduler[eventPublisherSchedulerSize];
        for (int i = 0; i < eventPublisherSchedulerSize; i++) {
            eventPublishSchedulers[i] = this.eventPublishSchedulerFactory.create(i);
            eventPublishSchedulers[i].start();
        }
        return eventPublishSchedulers;
    }

    protected final EventPublishScheduler findEventPublisherScheduler(String groupId) {
        int length = this.eventPublishSchedulers.length;
        return this.eventPublishSchedulers[groupId.hashCode() & (length - 1)];
    }

    protected void findBestSchedulerAndTryAddStateGroup(String groupId, Object event, Object... args) {
        StateGroupPool.FetchStateGroup fetchStateGroup = this.stateGroupPool.findOrCreate(groupId, args);
        StateGroup stateGroup = fetchStateGroup.stateGroup();
        if (!fetchStateGroup.isNew()) {
            stateGroup.addEvent(event);
            return;
        }
        final ConcurrentHashMap<Long, StateGroupScheduler> stateGroupSchedulerMap = this.stateGroupSchedulerMap;
        for (StateGroupScheduler scheduler : stateGroupSchedulerMap.values()) {
            if (scheduler.isFull()) {
                continue;
            }
            if (!scheduler.isStarted()) {
                continue;
            }
            if (scheduler.tryAddStateGroup(stateGroup)) {
                stateGroup.addEvent(event);
                return;
            }
        }

        if (this.isFull()) {
            log.error("can not add any stateGroupScheduler");
            throw new IllegalArgumentException("can not add any stateGroupScheduler");
        }
        StateGroupScheduler scheduler = this.stateGroupSchedulerFactory.create(stateGroupSchedulerIdCounter.getAndIncrement());
        scheduler.start();
        scheduler.tryAddStateGroup(stateGroup);
        stateGroup.addEvent(event);
        stateGroupSchedulerMap.put(scheduler.id(), scheduler);
        this.stateGroupSchedulerMap = stateGroupSchedulerMap;
    }


    @Override
    public void addEvent(String groupId, Object event, Object... args) {
        if (!this.isRunning) {
            throw new UnsupportedOperationException("schedulerManager has stopped");
        }
        this.findEventPublisherScheduler(groupId).publish(() -> this.findBestSchedulerAndTryAddStateGroup(groupId, event, args));
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public int size() {
        return this.stateGroupSchedulerMap.size();
    }

    @Override
    public synchronized boolean isFull() {
        return this.size() >= this.schedulerManagerConfig.getStateGroupSchedulerSize();
    }


    public void refresh() {
        final ConcurrentHashMap<Long, StateGroupScheduler> old = this.stateGroupSchedulerMap;
        final ConcurrentHashMap<Long, StateGroupScheduler> _new = new ConcurrentHashMap<>();
        int size = old.size();
        if (size == 0) {
            return;
        }
        for (StateGroupScheduler scheduler : old.values()) {
            if (scheduler.isEmpty() && !scheduler.isStarted() && size > this.schedulerManagerConfig.getStateGroupSchedulerCoreSize()) {
                scheduler.stop();
                size--;
                continue;
            }
            _new.put(scheduler.id(), scheduler);
        }
        STATE_GROUP_SCHEDULER_MAP_HANDLE.compareAndSet(this, old, _new);
    }

    @Override
    public void close() throws IOException {
        this.isRunning = false;
        Collection<StateGroupScheduler> values = this.stateGroupSchedulerMap.values();
        for (StateGroupScheduler scheduler : values) {
            scheduler.stop();
        }
        for (EventPublishScheduler scheduler : this.eventPublishSchedulers) {
            scheduler.stop();
        }
    }
}
