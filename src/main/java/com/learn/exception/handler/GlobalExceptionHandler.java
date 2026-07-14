package com.learn.exception.handler;

import cn.hutool.core.util.StrUtil;
import com.learn.domain.pojo.Result;
import com.learn.exception.ServiceException;
import com.learn.exception.enums.StatusCodeEnum;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Data;
@RestControllerAdvice
@Data
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value={ServiceException.class})
    public Result<?> handleServiceException(ServiceException e) {
        log.error("ServiceException ", (Throwable)e);
        return Result.fail((String)e.getMessage());
    }

    @ExceptionHandler(value={IllegalArgumentException.class})
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException ", (Throwable)e);
        return Result.fail((String)e.getMessage());
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException ", (Throwable)e);
        return Result.fail((Integer)StatusCodeEnum.VALID_ERROR.getCode(), (String)Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(value={ConstraintViolationException.class})
    public Result<?> handleValidationException(ConstraintViolationException e) {
        if (StrUtil.isNotBlank((CharSequence)e.getMessage())) {
            log.error("ConstraintViolationException ", (Throwable)e);
            return Result.fail((Integer)StatusCodeEnum.VALID_ERROR.getCode(), (String)e.getConstraintViolations().toString());
        }
        return Result.fail((String)StatusCodeEnum.VALID_ERROR.getMsg());
    }

    @ExceptionHandler(value={HttpMessageNotReadableException.class})
    public Result<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("json格式异常:", (Throwable)ex);
        return Result.fail((Integer)StatusCodeEnum.JSON_FORMAT_ERROR.getCode(), (String)StatusCodeEnum.JSON_FORMAT_ERROR.getMsg());
    }

    @ExceptionHandler(value={Exception.class})
    public Result<?> handleSystemException(Exception e) {
        log.error("系统异常:{e}", (Throwable)e);
        return Result.fail((Integer)StatusCodeEnum.SYSTEM_ERROR.getCode(), (String)StatusCodeEnum.SYSTEM_ERROR.getMsg());
    }
}
