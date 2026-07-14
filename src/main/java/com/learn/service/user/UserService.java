package com.learn.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.domain.dto.UserRegisterDTO;
import com.learn.domain.dto.login.UserLoginDTO;
import com.learn.domain.dto.user.AdminUpdatePasswordDTO;
import com.learn.domain.dto.user.UpdatePasswordDTO;
import com.learn.domain.dto.user.UpdateUserRoleDTO;
import com.learn.domain.pojo.PageResult;
import com.learn.domain.vo.WikiListVO;
import com.learn.domain.vo.token.LoginTokenVO;
import com.learn.domain.vo.user.UserVO;
import com.learn.models.entity.UserDO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
public interface UserService
extends IService<UserDO> {
    public UserVO register(@Valid UserRegisterDTO var1);

    public LoginTokenVO login(@Valid UserLoginDTO var1);

    public LoginTokenVO.User getLoginUser();

    public HashMap<String, Object> checkPhoneNumber(String var1);

    public void removeAccount();

    public HashMap<String, Object> refreshToken(@NotNull(message="请选择是否刷新accessToken") @NotNull(message="请选择是否刷新accessToken") Boolean var1);

    public void updateProfile(MultipartFile var1, String var2);

    public void updatePassword(@Valid UpdatePasswordDTO var1);

    public void logout(HttpServletRequest var1);

    public PageResult<LoginTokenVO.User> getAllUsers(Long var1, Long var2);

    public LoginTokenVO.User getUser(Integer var1);

    public void updateUserRole(Integer var1, UpdateUserRoleDTO var2);

    public void updateUserPassword(Integer var1, AdminUpdatePasswordDTO var2);

    public PageResult<WikiListVO> getWikis(String var1, String var2, String var3, String var4, List<String> var5, String var6);

    public void submitWikiApproval(String var1);
}
