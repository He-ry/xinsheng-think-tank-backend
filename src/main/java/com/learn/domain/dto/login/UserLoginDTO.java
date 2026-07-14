package com.learn.domain.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @Schema(description="用户名")
    @NotBlank(message="用户名不能为空")
    private @NotBlank(message="用户名不能为空") String username;
    @Schema(description="密码")
    @NotBlank(message="密码不能为空")
    private @NotBlank(message="密码不能为空") String password;
    @Schema(description="xinsheng模型接口token")
    private String token;
}
