package cn.monkey.state.core;


import cn.monkey.state.util.Timer;

public class EmptyState extends AbstractState {

    public static final String CODE = "empty";

    public EmptyState(StateGroup stateGroup) {
        super(CODE, stateGroup);
    }

    @Override
    public String finish(Timer timer) throws Exception {
        return null;
    }
}
