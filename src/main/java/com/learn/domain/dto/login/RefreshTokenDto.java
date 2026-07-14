package com.learn.domain.dto.login;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class RefreshTokenDto {
    @NotNull(message="请选择是否刷新accessToken")
    private @NotNull(message="请选择是否刷新accessToken") Boolean wantRefreshAccessToken;public void setWantRefreshAccessToken(Boolean wantRefreshAccessToken) {
        this.wantRefreshAccessToken = wantRefreshAccessToken;
    }
}
