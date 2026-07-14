package com.learn.exception.enums;

import com.learn.exception.enums.StatusCodeProvider;
public enum StatusCodeEnum implements StatusCodeProvider
{
    SUCCESS(Integer.valueOf(200), "操作成功"),
    VALID_ERROR(Integer.valueOf(400), "参数错误"),
    JSON_FORMAT_ERROR(Integer.valueOf(400), "JSON格式错误"),
    SYSTEM_ERROR(Integer.valueOf(-1), "系统异常"),
    FAIL(Integer.valueOf(500), "操作失败"),
    TOO_MANY_REQUESTS(Integer.valueOf(429), "请求过于频繁");

    private final Integer code;
    private final String msg;
    public Integer getCode() {
        return this.code;
    }


    public String getMsg() {
        return this.msg;
    }
    private StatusCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
