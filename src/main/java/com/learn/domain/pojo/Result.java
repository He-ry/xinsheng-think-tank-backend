package com.learn.domain.pojo;

import com.learn.exception.enums.StatusCodeEnum;
import com.learn.exception.enums.StatusCodeProvider;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T>
implements Serializable {

    private Integer code;
    private String message;
    private T data;
}
