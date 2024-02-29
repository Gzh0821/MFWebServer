package cn.monkey.state.core;


import cn.monkey.commons.bean.Refreshable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleStateGroupPool implements StateGroupPool, Refreshable {

    protected final StateGroupFactory stateGroupFactory;
    protected volatile ConcurrentHashMap<String, StateGroup> stateGroupMap;
    static final VarHandle STATE_GROUP_MAP_HANDLE;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            STATE_GROUP_MAP_HANDLE = lookup.findVarHandle(SimpleStateGroupPool.class, "stateGroupMap", ConcurrentHashMap.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleStateGroupPool(StateGroupFactory stateGroupFactory) {
        this.stateGroupMap = new ConcurrentHashMap<>();
        this.stateGroupFactory = stateGroupFactory;
    }

    @Override
    public FetchStateGroup findOrCreate(String id, Object... args) {
        boolean[] isNew = {false};
        final ConcurrentHashMap<String, StateGroup> stateGroupMap = this.stateGroupMap;
        StateGroup stateGroup = stateGroupMap.computeIfAbsent(id, (key) -> {
            isNew[0] = true;
            return this.stateGroupFactory.create(key, args);
        });
        this.stateGroupMap = stateGroupMap;
        return new FetchStateGroup(isNew[0], stateGroup);
    }

    @Override
    public void refresh() {
        final ConcurrentHashMap<String, StateGroup> stateGroupMap = this.stateGroupMap;
        ConcurrentHashMap<String, StateGroup> newStateGroupMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, StateGroup> e : stateGroupMap.entrySet()) {
            StateGroup value = e.getValue();
            if (value.canClose()) {
                value.close();
                continue;
            }
            value.flush();
            newStateGroupMap.put(e.getKey(), e.getValue());
        }
        STATE_GROUP_MAP_HANDLE.compareAndSet(this, stateGroupMap, newStateGroupMap);
    }
}
