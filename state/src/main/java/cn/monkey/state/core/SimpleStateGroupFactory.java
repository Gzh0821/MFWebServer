package cn.monkey.state.core;


import cn.monkey.state.util.Timer;

public class SimpleStateGroupFactory implements StateGroupFactory {

    protected final Timer timer;

    public SimpleStateGroupFactory(Timer timer) {
        this.timer = timer;
    }


    @Override
    public StateGroup create(String id, Object... args) {
        StateGroup stateGroup = new SimpleStateGroup(id, StateContext.EMPTY, this.timer);
        State state = new EmptyState(stateGroup);
        stateGroup.addState(state);
        stateGroup.setStartState(EmptyState.CODE);
        return stateGroup;
    }
}
