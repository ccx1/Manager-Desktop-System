package com.example.manager.result;

import com.example.manager.enums.HttpCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
public class ResultEntity<T> {

    private int code;

    private String msg;

    @JsonInclude(NON_NULL)
    private T data;

    private long time = System.currentTimeMillis();


    public static <T> ResultEntity<T> ok() {
        return ok(null);
    }

    public static <T> ResultEntity<T> noContent() {
        return ok(null);
    }


    public static <T> ResultEntity<T> ok(T data) {
        ResultEntity<T> entity = new ResultEntity<T>();
        entity.setCode(HttpCode.OK.getCode());
        entity.setMsg(HttpCode.OK.getMsg());
        entity.setData(data);
        return entity;
    }


    public static ResultEntity<Void> fail(HttpCode httpCode, Throwable e) {
        ResultEntity<Void> entity = new ResultEntity<Void>();
        entity.setCode(httpCode.getCode());
        entity.setMsg(e == null ? httpCode.getMsg() : e.getMessage());
        return entity;
    }


    public static ResultEntity<Void> fail(HttpCode httpCode) {
        return fail(httpCode, null);
    }


    public static ResultEntity<Void> fail(Exception e) {
        return fail(HttpCode.UN_KNOW_ERROR, e);
    }

    public static ResultEntity<Void> fail(Throwable e) {
       if ( e instanceof CodeException){
          return fail( ((CodeException) e));
       }
        return fail(HttpCode.UN_KNOW_ERROR, e);
    }

    public static ResultEntity<Void> fail(CodeException e) {
        ResultEntity<Void> entity = new ResultEntity<Void>();
        entity.setCode(e.getCode());
        entity.setMsg(e.getMessage());
        return entity;
    }
}
