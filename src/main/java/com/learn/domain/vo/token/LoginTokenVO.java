package com.learn.domain.vo.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginTokenVO {

    @Schema(description = "访问令牌")
    private String accessToken;
    @Schema(description = "刷新令牌")
    private String refreshToken;
    @Schema(description = "访问令牌有效期")
    private Long accessTokenExpiresData;
    @Schema(description = "刷新令牌有效期")
    private Long refreshTokenExpiresData;
    @Schema(description = "用户信息")
    private User user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private Integer id;
        private String username;
        private String phoneNumber;
        private Long createdAt;
        private String role;
        private Boolean isActive;
        private Long level;
        private Long experience;
        private String avatar;
    }
}
