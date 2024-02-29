package cn.monkey.data.server.state;

import cn.monkey.state.core.AbstractStateGroup;
import cn.monkey.state.core.State;
import cn.monkey.state.core.StateInfo;
import cn.monkey.state.util.Timer;

import java.util.*;

public class MsgGroup extends AbstractStateGroup {
    public MsgGroup(String id, MessageContext stateContext, Timer timer) {
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

    @Override
    public MessageContext getStateContext() {
        return (MessageContext) super.getStateContext();
    }

    @Override
    public void update() {
        if (this.eventQueue.isEmpty()) {
            return;
        }
        ArrayList<Object> list = new ArrayList<>(this.eventQueue);
        list.forEach(e -> {
            try {
                this.currentState.fireEvent(this.timer, e);
            } catch (Exception ex) {
                this.currentState.onFireEventError(this.timer, e, ex);
            }
        });
        StateInfo stateInfo = new StateInfo();
        try {
            this.currentState.update(this.timer, stateInfo);
        } catch (Exception e) {
            this.currentState.onUpdateError(this.timer, stateInfo, e);
        }
    }
}
