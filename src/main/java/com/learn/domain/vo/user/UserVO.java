package com.learn.domain.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    @Schema(description="用户ID")
    private Integer id;
    @Schema(description="用户昵称")
    private String username;
    @Schema(description="手机号")
    private String phoneNumber;
    @Schema(description="创建时间，Unix时间戳，秒级")
    private Long createdAt;
    @Schema(description="用户角色")
    private String role;
    @Schema(description="帐号状态（0正常 1停用）")
    private Boolean isActive;
    @Schema(description="用户等级")
    private Long level;
    @Schema(description="用户头像地址")
    private String avatar;
    @Schema(description="用户经验值")
    private Long experience;
}
