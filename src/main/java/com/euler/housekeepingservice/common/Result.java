package com.euler.housekeepingservice.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 全局统一返回结果类
 */
@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    // 成功响应封装
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 失败响应封装
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}