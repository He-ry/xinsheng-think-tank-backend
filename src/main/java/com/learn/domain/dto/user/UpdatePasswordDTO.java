package com.learn.domain.dto.user;

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
public class UpdatePasswordDTO {
    @Schema(description="新密码")
    @NotBlank(message="新密码不能为空")
    private @NotBlank(message="新密码不能为空") String newPassword;
    @Schema(description="旧密码")
    @NotBlank(message="旧密码不能为空")
    private @NotBlank(message="旧密码不能为空") String oldPassword;public String getNewPassword() {
        return this.newPassword;
    }
}
