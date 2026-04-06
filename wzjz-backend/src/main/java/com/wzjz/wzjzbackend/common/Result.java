package com.wzjz.wzjzbackend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 200表示成功，500表示失败
    private String msg;   // 提示信息
    private T data;       // 实际返回的数据

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}