package cn.monkey.state.core;



import cn.monkey.state.util.Timer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SimpleStateGroup extends AbstractStateGroup {

    public SimpleStateGroup(String id, StateContext stateContext, Timer timer) {
        super(id, stateContext, timer);
    }

    @Override
    protected Queue<Object> createEventQueue() {
        return new LinkedList<>();
    }

    @Override
    protected Map<String, State> createStateMap() {
        return new HashMap<>();
    }
}
