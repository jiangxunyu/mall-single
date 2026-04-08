package com.mall.po.vo;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static Result success(Object data){
        Result r = new Result();
        r.code = 200;
        r.data = data;
        r.msg = "操作成功";
        return r;
    }

    public static Result success(String msg){
        Result r = new Result();
        r.code = 200;
        r.msg = msg;
        return r;
    }

    public static Result error(String msg){
        Result r = new Result();
        r.code = 500;
        r.msg = msg;
        return r;
    }
}