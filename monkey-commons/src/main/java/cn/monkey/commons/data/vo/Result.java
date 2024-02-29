package cn.monkey.commons.data.vo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.function.Function;

@Getter
@Builder(access = AccessLevel.PACKAGE)
public class Result<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
    private Throwable error;

    public <R> Result<R> map(Function<T, R> func) {
        return Results.map(this, func);
    }

    @Tolerate
    private Result() {
    }

}
