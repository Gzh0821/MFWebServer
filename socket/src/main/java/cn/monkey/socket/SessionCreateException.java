package cn.monkey.socket;

public class SessionCreateException extends RuntimeException {
    public SessionCreateException() {
    }

    public SessionCreateException(String message) {
        super(message);
    }

    public SessionCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionCreateException(Throwable cause) {
        super(cause);
    }

    public SessionCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
