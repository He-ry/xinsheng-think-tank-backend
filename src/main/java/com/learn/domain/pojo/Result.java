package com.learn.domain.pojo;

import com.learn.exception.enums.StatusCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(StatusCodeEnum.SUCCESS.getCode())
                .message(StatusCodeEnum.SUCCESS.getMsg())
                .build();
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(StatusCodeEnum.SUCCESS.getCode())
                .message(StatusCodeEnum.SUCCESS.getMsg())
                .data(data)
                .build();
    }

    public static <T> Result<T> fail(String message) {
        return Result.<T>builder()
                .code(StatusCodeEnum.FAIL.getCode())
                .message(message)
                .build();
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
