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
public class UpdateUserRoleDTO {
    @Schema(description="用户角色")
    @NotBlank(message="用户角色不能为空")
    private @NotBlank(message="用户角色不能为空") String role;public String getRole() {
        return this.role;
    }
}
