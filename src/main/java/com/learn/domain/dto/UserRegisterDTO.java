package com.learn.domain.dto;

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
public class UserRegisterDTO {

    @NotBlank(message="用户昵称不能为空")
    @Schema(description="用户昵称")
    private @NotBlank(message="用户昵称不能为空") String username;
    @NotBlank(message="手机号不能为空")
    @Schema(description="手机号")
    private @NotBlank(message="手机号不能为空") String phoneNumber;
    @NotBlank(message="密码不能为空")
    @Schema(description="密码")
    private @NotBlank(message="密码不能为空") String password;
    @Schema(description="用户角色")
    private String role;
}
