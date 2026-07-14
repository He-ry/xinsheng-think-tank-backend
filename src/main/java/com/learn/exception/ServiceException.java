package com.learn.exception;

import com.learn.exception.enums.StatusCodeEnum;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final Integer code;
    private final String message;

    public ServiceException(String message) {
        this(StatusCodeEnum.FAIL.getCode(), message);
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
