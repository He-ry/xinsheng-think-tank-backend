package com.learn.exception.enums;

import lombok.Data;
public interface StatusCodeProvider {
    public Integer getCode();

    public String getMsg();
}
