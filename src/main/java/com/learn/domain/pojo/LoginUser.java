package com.learn.domain.pojo;

import com.learn.models.enums.RoleEnum;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Integer userId;
    private String username;
    private String role;

    public boolean isSuperAdmin() {
        return RoleEnum.SUPER_ADMIN.getCode().equals(this.role);
    }
}
