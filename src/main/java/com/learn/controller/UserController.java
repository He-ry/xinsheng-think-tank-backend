package com.learn.controller;

import com.learn.domain.dto.UserRegisterDTO;
import com.learn.domain.dto.login.RefreshTokenDto;
import com.learn.domain.dto.login.UserLoginDTO;
import com.learn.domain.dto.user.AdminUpdatePasswordDTO;
import com.learn.domain.dto.user.UpdatePasswordDTO;
import com.learn.domain.dto.user.UpdateUserRoleDTO;
import com.learn.domain.pojo.PageResult;
import com.learn.domain.pojo.Result;
import com.learn.domain.vo.WikiListVO;
import com.learn.domain.vo.token.LoginTokenVO;
import com.learn.domain.vo.user.UserVO;
import com.learn.domain.vo.user.XinShengUserInfo;
import com.learn.exception.ServiceException;
import com.learn.models.enums.WikiStateEnum;
import com.learn.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="用户注册")
@RestController
@RequestMapping(value={"/api/v1/user"})
@Validated
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping(value={"/auth"})
    @Operation(summary="用户认证接口")
    public Result<HashMap<String, Object>> save(@RequestBody @Valid UserRegisterDTO dto) {
        UserVO res = this.userService.register(dto);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user", res);
        map.put("xinShengUserInfo", new XinShengUserInfo());
        return Result.success(map);
    }

    @PostMapping(value={"/login"})
    @Operation(summary="用户登录接口")
    public Result<LoginTokenVO> login(@RequestBody @Valid UserLoginDTO dto) {
        LoginTokenVO res = this.userService.login(dto);
        return Result.success((Object)res);
    }

    @GetMapping(value={"/check"})
    @Operation(summary=" 检查手机号是否可用")
    public Result<HashMap<String, Object>> check(@RequestParam(value="phoneNumber") String phoneNumber) {
        HashMap res = this.userService.checkPhoneNumber(phoneNumber);
        return Result.success((Object)res);
    }

    @PostMapping(value={"/cancel-account"})
    @Operation(summary="注销账号")
    public Result<HashMap<String, Object>> cancelAccount() {
        this.userService.removeAccount();
        return Result.success(new HashMap());
    }

    @PostMapping(value={"/refresh-token"})
    @Operation(summary="刷新token")
    public Result<HashMap<String, Object>> refreshToken(@RequestBody @Valid RefreshTokenDto dto) {
        HashMap res = this.userService.refreshToken(dto.getWantRefreshAccessToken());
        return Result.success((Object)res);
    }

    @GetMapping(value={"/profile"})
    @Operation(summary="获取用户信息")
    public Result<LoginTokenVO.User> getProfile() {
        LoginTokenVO.User res = this.userService.getLoginUser();
        return Result.success((Object)res);
    }

    @PostMapping(value={"/profile"})
    @Operation(summary="更新当前用户信息")
    public Result<HashMap<String, Object>> updateProfile(@RequestParam(required=false, name="avatar") MultipartFile avatar, @RequestParam(required=false, name="username") String username) {
        this.userService.updateProfile(avatar, username);
        return Result.success(new HashMap());
    }

    @PostMapping(value={"/password"})
    @Operation(summary="修改密码")
    public Result<HashMap<String, Object>> updatePassword(@RequestBody @Valid UpdatePasswordDTO dto) {
        this.userService.updatePassword(dto);
        return Result.success(new HashMap());
    }

    @PostMapping(value={"/logout"})
    @Operation(summary="注销登录")
    public Result<HashMap<String, Object>> logout(HttpServletRequest request) {
        this.userService.logout(request);
        return Result.success(new HashMap());
    }

    @GetMapping(value={"/admin/users"})
    @Operation(summary="获取所有用户")
    public Result<PageResult<LoginTokenVO.User>> getAllUsers(@RequestParam(required=false, defaultValue="1") Long page, @RequestParam(required=false, defaultValue="10") Long perPage) {
        PageResult allUsers = this.userService.getAllUsers(page, perPage);
        return Result.success((Object)allUsers);
    }

    @GetMapping(value={"/admin/users/{userId}"})
    @Operation(summary="获取用户信息")
    public Result<LoginTokenVO.User> getUser(@PathVariable(value="userId") Integer userId) {
        if (userId == null) {
            throw new ServiceException("用户id不能为空");
        }
        LoginTokenVO.User user = this.userService.getUser(userId);
        return Result.success((Object)user);
    }

    @PostMapping(value={"/admin/users/{userId}/role"})
    @Operation(summary="修改用户权限")
    public Result<HashMap<String, Object>> updateUserRole(@PathVariable(value="userId") Integer userId, @RequestBody UpdateUserRoleDTO role) {
        if (userId == null) {
            throw new ServiceException("用户id不能为空");
        }
        this.userService.updateUserRole(userId, role);
        return Result.success(new HashMap());
    }

    @PostMapping(value={"/admin/users/{userId}/password"})
    @Operation(summary="修改用户密码")
    public Result<HashMap<String, Object>> updateUserPassword(@PathVariable(value="userId") Integer userId, @RequestBody AdminUpdatePasswordDTO dto) {
        if (userId == null) {
            throw new ServiceException("用户id不能为空");
        }
        this.userService.updateUserPassword(userId, dto);
        return Result.success(new HashMap());
    }

    @GetMapping(value={"/wikis"})
    @Operation(summary="获取用户所有知识库")
    public Result<PageResult<WikiListVO>> getWikis(@RequestParam(value="page", required=false, defaultValue="1") String pageNum, @RequestParam(value="perPage", required=false, defaultValue="10") String pageSize, @RequestParam(value="sortBy", required=false, defaultValue="update_at") String sortBy, @RequestParam(value="sortOrder", required=false, defaultValue="desc") String order, @RequestParam(value="q", required=false, defaultValue="") String q, @RequestParam(value="status", required=false, defaultValue="all") String status) {
        List<String> statusList = WikiStateEnum.ALL.getCode().equals(status) ? WikiStateEnum.getAllState() : List.of(status);
        PageResult wikis = this.userService.getWikis(pageNum, pageSize, sortBy, order, statusList, q);
        return Result.success((Object)wikis);
    }

    @GetMapping(value={"/submit-wiki-approval/{id}"})
    @Operation(summary="提交知识库审核")
    public Result<String> submitWikiApproval(@PathVariable(value="id") String id) {
        if (id == null) {
            throw new ServiceException("知识库id不能为空");
        }
        this.userService.submitWikiApproval(id);
        return Result.success();
    }
}
