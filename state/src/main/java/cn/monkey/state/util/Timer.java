package cn.monkey.state.util;

public interface Timer {
    default long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }
}
