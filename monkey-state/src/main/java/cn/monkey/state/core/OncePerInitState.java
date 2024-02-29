package cn.monkey.state.core;


import cn.monkey.state.util.Timer;

public abstract class OncePerInitState extends AbstractState {

    protected boolean hasInit = false;

    public OncePerInitState(String code, StateGroup stateGroup) {
        super(code, stateGroup);
    }


    @Override
    public final void init(Timer timer) throws Exception {
        if (this.hasInit) {
            return;
        }
        this.onInit();
        this.hasInit = true;
    }

    protected abstract void onInit();
}
