package cn.monkey.commons.data.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Function;

public interface Results {

    static <T> Result<T> ok(T data) {
        return Result.<T>builder().code(ResultCode.OK).data(data).build();
    }

    static <T> Result<T> ok() {
        return ok(null);
    }

    static <T> Result<T> fail(int code, String msg) {
        return Result.<T>builder().code(code).msg(msg).build();
    }

    static <T> Result<T> fail(String msg) {
        return fail(ResultCode.FAIL, msg);
    }

    static boolean isOK(Result<?> result) {
        return ResultCode.OK == result.getCode();
    }


    static <T> Result<T> error(Throwable error) {
        return Result.<T>builder().code(ResultCode.ERROR).error(error).build();
    }

    static <T> Result<T> unKnown(String msg, T data, Throwable error) {
        return Result.<T>builder().code(ResultCode.UNKNOWN).msg(msg).data(data).error(error).build();
    }

    static <T> Result<T> fromNullData(Result<?> result) {
        return Result.<T>builder().code(result.getCode()).msg(result.getMsg()).error(result.getError()).build();
    }

    static <R, T> Result<R> map(Result<T> result, Function<T, R> func) {
        T data = result.getData();
        if (Objects.isNull(data)) {
            return fromNullData(result);
        }
        R apply = func.apply(data);
        return Result.<R>builder()
                .code(result.getCode())
                .msg(result.getMsg())
                .data(apply)
                .build();
    }

    Type MAP_TYPE = new TypeToken<LinkedHashMap<String, Object>>() {
    }.getType();

    static <T> Result<T> fromJsonStr(String s, Gson gson, Class<T> clazz) {
        return fromJsonStr(s, gson, (Type) clazz);
    }

    static <T> Result<T> fromJsonStr(String s, Class<T> clazz) {
        return fromJsonStr(s, gson, clazz);
    }

    static <T> Result<T> fromJsonStr(String s, Type type) {
        return fromJsonStr(s, gson, type);
    }

    static <T> Result<T> fromJsonStr(String s, Gson gson, Type type) {
        LinkedHashMap<String, Object> json = gson.fromJson(s, MAP_TYPE);
        Object code = json.get("code");
        Object msg = json.get("msg");
        Object data = json.get("data");
        Result.ResultBuilder<T> builder = Result.builder();
        builder.code((int) Double.parseDouble(String.valueOf(code)));
        if (msg != null) {
            builder.msg(String.valueOf(msg));
        }
        if (data != null) {
            builder.data(gson.fromJson(gson.toJson(data), type));
        }
        return builder.build();
    }

    Gson gson = new Gson();


    static String toJson(Result<?> result) {
        return gson.toJson(result);
    }
}
