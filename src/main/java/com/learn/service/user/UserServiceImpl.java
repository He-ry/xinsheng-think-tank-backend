package com.learn.service.user;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.config.filter.TokenBlacklist;
import com.learn.domain.dto.UserRegisterDTO;
import com.learn.domain.dto.login.UserLoginDTO;
import com.learn.domain.dto.user.AdminUpdatePasswordDTO;
import com.learn.domain.dto.user.UpdatePasswordDTO;
import com.learn.domain.dto.user.UpdateUserRoleDTO;
import com.learn.domain.pojo.LoginUser;
import com.learn.domain.pojo.PageResult;
import com.learn.domain.pojo.Pagination;
import com.learn.domain.pojo.UserContext;
import com.learn.domain.vo.WikiListVO;
import com.learn.domain.vo.token.LoginTokenVO;
import com.learn.domain.vo.user.UserVO;
import com.learn.exception.ServiceException;
import com.learn.models.entity.RoleDO;
import com.learn.models.entity.ThinkTankDO;
import com.learn.models.entity.UserDO;
import com.learn.models.entity.UserRoleDO;
import com.learn.models.enums.RoleEnum;
import com.learn.models.enums.WikiStateEnum;
import com.learn.models.mapper.UserMapper;
import com.learn.service.thinktank.ThinkTankService;
import com.learn.service.user.UserService;
import com.learn.service.userrole.UserRoleService;
import com.learn.utils.FileUtils;
import com.learn.utils.JwtUtil;
import com.learn.utils.PasswordUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Service
@Validated
public class UserServiceImpl
extends ServiceImpl<UserMapper, UserDO>
implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Value("${jwt.access-token.default-expire-seconds}")
    private long ACCESS_EXPIRE_SECONDS;
    @Value("${jwt.refresh-token.default-expire-seconds}")
    private long REFRESH_EXPIRE_SECONDS;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private FileUtils fileUtils;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private ThinkTankService thinkTankService;

    public UserVO register(UserRegisterDTO dto) {
        List<UserDO> list = this.lambdaQuery().eq(UserDO::getPhoneNumber, dto.getPhoneNumber()).list();
        if (CollUtil.isNotEmpty(list)) {
            UserDO first = list.getFirst();
            log.info("用户已在信胜智库注册:{}", dto);
            List<RoleDO> roleDOList = this.userRoleService.getUserRole(first.getId());
            if (!dto.getUsername().equals(first.getUsername()) || !PasswordUtil.encode(dto.getPassword()).equals(first.getPassword())) {
                log.info("用户名不一致或密码不一致:{}", dto);
                first.setUsername(dto.getUsername());
                first.setPassword(PasswordUtil.encode(dto.getPassword()));
                this.updateById(first);
            }
            return UserVO.builder()
                .id(first.getId())
                .username(first.getUsername())
                .phoneNumber(first.getPhoneNumber())
                .createdAt(first.getCreatedAt())
                .isActive(first.getIsActivate())
                .level(first.getLevel())
                .role(CollUtil.isEmpty(roleDOList) ? RoleEnum.NORMAL.getCode() : roleDOList.getFirst().getCode())
                .experience(first.getExperience())
                .build();
        }
        String password = dto.getPassword();
        String phoneNumber = dto.getPhoneNumber();
        if (!PhoneUtil.isPhone(phoneNumber)) {
            log.info("手机号格式错误:{}", phoneNumber);
            throw new ServiceException("手机号格式不正确！");
        }
        String encodedPwd = PasswordUtil.encode(password);
        UserDO user = UserDO.builder()
            .username(dto.getUsername())
            .password(encodedPwd)
            .phoneNumber(dto.getPhoneNumber())
            .createdAt(Instant.now().getEpochSecond())
            .build();
        this.lambdaQuery().eq(UserDO::getUsername, dto.getUsername()).oneOpt().ifPresent(userDO -> {
            throw new ServiceException("用户已经存在");
        });
        this.saveOrUpdate(user);
        UserDO userDO2 = this.getById(user.getId());
        this.userRoleService.insertUserRole(userDO2.getId(), RoleEnum.NORMAL.getId());
        return UserVO.builder()
            .id(userDO2.getId())
            .username(userDO2.getUsername())
            .phoneNumber(userDO2.getPhoneNumber())
            .createdAt(userDO2.getCreatedAt())
            .isActive(userDO2.getIsActivate())
            .level(userDO2.getLevel())
            .role(RoleEnum.NORMAL.name())
            .experience(userDO2.getExperience())
            .avatar(userDO2.getAvatar() == null ? "" : userDO2.getAvatar())
            .build();
    }

    public LoginTokenVO login(UserLoginDTO dto) {
        UserDO user = this.lambdaQuery().eq(UserDO::getUsername, dto.getUsername()).one();
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        List<RoleDO> roleDOList = this.userRoleService.getUserRole(user.getId());
        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new ServiceException("密码错误");
        }
        if (!Boolean.TRUE.equals(user.getIsActivate())) {
            user.setIsActivate(true);
            user.setDeleteTime(null);
            this.updateById(user);
        }
        Integer userId = user.getId();
        StpUtil.login(userId);
        String accessToken = this.jwtUtil.generate(userId, user.getUsername(), roleDOList.getFirst().getCode(), this.ACCESS_EXPIRE_SECONDS);
        String refreshToken = this.jwtUtil.generate(userId, user.getUsername(), roleDOList.getFirst().getCode(), this.REFRESH_EXPIRE_SECONDS);
        LoginTokenVO.User userVO = LoginTokenVO.User.builder()
            .id(userId)
            .username(user.getUsername())
            .phoneNumber(user.getPhoneNumber())
            .createdAt(user.getCreatedAt())
            .role(CollUtil.isEmpty(roleDOList) ? RoleEnum.NORMAL.getCode() : roleDOList.getFirst().getCode())
            .isActive(user.getIsActivate())
            .level(user.getLevel() == null ? 0L : user.getLevel().longValue())
            .experience(user.getExperience() == null ? 0L : user.getExperience().longValue())
            .build();
        long now = Instant.now().getEpochSecond();
        return LoginTokenVO.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .accessTokenExpiresData(now + this.ACCESS_EXPIRE_SECONDS)
            .refreshTokenExpiresData(now + this.REFRESH_EXPIRE_SECONDS)
            .user(userVO)
            .build();
    }

    public LoginTokenVO.User getLoginUser() {
        Integer userId = UserContext.getUserId();
        UserDO userDO = this.getById(userId);
        List<RoleDO> userRole = this.userRoleService.getUserRole(userId);
        return LoginTokenVO.User.builder()
            .id(userDO.getId())
            .username(userDO.getUsername())
            .phoneNumber(userDO.getPhoneNumber())
            .createdAt(userDO.getCreatedAt())
            .role(userRole.getFirst().getCode())
            .isActive(userDO.getIsActivate())
            .level(userDO.getLevel() == null ? 0L : userDO.getLevel().longValue())
            .experience(userDO.getExperience() == null ? 0L : userDO.getExperience().longValue())
            .avatar(userDO.getAvatar() == null ? "" : userDO.getAvatar())
            .build();
    }

    public HashMap<String, Object> checkPhoneNumber(String phoneNumber) {
        if (StrUtil.isEmpty(phoneNumber)) {
            throw new ServiceException("手机号不能为空");
        }
        if (!PhoneUtil.isPhone(phoneNumber)) {
            throw new ServiceException("手机号格式错误");
        }
        HashMap<String, Object> res = new HashMap<>();
        List<UserDO> list = this.lambdaQuery().eq(UserDO::getPhoneNumber, phoneNumber).list();
        if (CollUtil.isEmpty(list)) {
            res.put("available", true);
            res.put("message", "手机号可用，无人注册该手机号。");
            return res;
        }
        res.put("available", false);
        res.put("message", "手机号已存在，请勿重复注册。");
        return res;
    }

    public void removeAccount() {
        LoginUser loginUser = UserContext.getLoginUser();
        if (loginUser == null) {
            throw new ServiceException("请先登录!");
        }
        if (loginUser.isSuperAdmin()) {
            throw new ServiceException("超级管理员不能注销！");
        }
        this.lambdaUpdate()
            .eq(UserDO::getId, loginUser.getUserId())
            .set(UserDO::getIsActivate, false)
            .set(UserDO::getDeleteTime, Instant.now().getEpochSecond() + 259200000L)
            .update();
    }

    public HashMap<String, Object> refreshToken(Boolean wantRefreshAccessToken) {
        HashMap<String, Object> map = new HashMap<>();
        LoginUser loginUser = UserContext.getLoginUser();
        long now = Instant.now().getEpochSecond();
        if (wantRefreshAccessToken) {
            long expiresAt = now + this.ACCESS_EXPIRE_SECONDS;
            String token = this.jwtUtil.generate(loginUser.getUserId(), loginUser.getUsername(), loginUser.getRole(), this.ACCESS_EXPIRE_SECONDS);
            map.put("accessToken", token);
            map.put("accessTokenExpiresData", expiresAt);
        } else {
            long expiresAt = now + this.REFRESH_EXPIRE_SECONDS;
            String token = this.jwtUtil.generate(loginUser.getUserId(), loginUser.getUsername(), loginUser.getRole(), this.REFRESH_EXPIRE_SECONDS);
            map.put("refreshToken", token);
            map.put("refreshTokenExpiresData", expiresAt);
        }
        return map;
    }

    public void updateProfile(MultipartFile avatar, String username) {
        String avatarUrl = null;
        if (avatar != null) {
            avatarUrl = this.fileUtils.uploadToLocal(avatar, this.uploadDir);
        }
        this.lambdaUpdate()
            .eq(UserDO::getId, UserContext.getUserId())
            .set(StrUtil.isNotEmpty(username), UserDO::getUsername, username)
            .set(avatar != null, UserDO::getAvatar, avatarUrl)
            .update();
    }

    public void updatePassword(UpdatePasswordDTO dto) {
        Integer userId = UserContext.getUserId();
        log.info("userId: {}", userId);
        List<UserDO> userList = this.lambdaQuery()
            .eq(UserDO::getId, UserContext.getUserId())
            .select(UserDO::getPassword)
            .list();
        if (CollUtil.isEmpty(userList)) {
            throw new ServiceException("用户不存在");
        }
        UserDO userDO = userList.getFirst();
        boolean matches = PasswordUtil.matches(dto.getOldPassword(), userDO.getPassword());
        if (!matches) {
            throw new ServiceException("旧密码错误");
        }
        if (PasswordUtil.matches(dto.getNewPassword(), userDO.getPassword())) {
            throw new ServiceException("新密码不能与旧密码相同");
        }
        if (dto.getNewPassword().length() < 6 || dto.getNewPassword().length() > 20) {
            throw new ServiceException("密码长度必须在6到20位之间");
        }
        this.lambdaUpdate()
            .eq(UserDO::getId, UserContext.getUserId())
            .set(UserDO::getPassword, PasswordUtil.encode(dto.getNewPassword()))
            .update();
    }

    public void logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        Claims claims = this.jwtUtil.parse(token);
        long expire = claims.getExpiration().getTime();
        TokenBlacklist.add(token, expire);
    }

    public PageResult<LoginTokenVO.User> getAllUsers(Long page, Long perPage) {
        Page<UserDO> pageParam = new Page<>(page, perPage);
        Page<UserDO> pageResult = this.lambdaQuery().page(pageParam);
        List<UserDO> records = pageResult.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            HashMap<Integer, List<RoleDO>> userRoleByUserIds = this.userRoleService.getUserRoleByUserIds(records.stream().map(UserDO::getId).toList());
            List<LoginTokenVO.User> collect = records.stream().map(item -> {
                List<RoleDO> roles = userRoleByUserIds.getOrDefault(item.getId(), new ArrayList<>());
                return LoginTokenVO.User.builder()
                    .id(item.getId())
                    .username(item.getUsername())
                    .phoneNumber(item.getPhoneNumber())
                    .createdAt(item.getCreatedAt())
                    .role(CollUtil.isEmpty(roles) ? RoleEnum.NORMAL.getCode() : roles.getFirst().getCode())
                    .isActive(item.getIsActivate())
                    .level(item.getLevel() == null ? 0L : item.getLevel().longValue())
                    .experience(item.getExperience() == null ? 0L : item.getExperience().longValue())
                    .avatar(item.getAvatar())
                    .build();
            }).collect(Collectors.toList());
            return new PageResult<>(collect, pageResult.getTotal(), page, perPage);
        }
        return new PageResult<>(new ArrayList<>(), pageResult.getTotal(), page, perPage);
    }

    public LoginTokenVO.User getUser(Integer userId) {
        List<UserDO> list = this.lambdaQuery().eq(UserDO::getId, userId).list();
        if (CollUtil.isEmpty(list)) {
            throw new ServiceException("用户不存在");
        }
        UserDO userDO = list.getFirst();
        List<RoleDO> roles = this.userRoleService.getUserRole(userId);
        return LoginTokenVO.User.builder()
            .id(userDO.getId())
            .username(userDO.getUsername())
            .phoneNumber(userDO.getPhoneNumber())
            .createdAt(userDO.getCreatedAt())
            .role(CollUtil.isEmpty(roles) ? RoleEnum.NORMAL.getCode() : roles.getFirst().getCode())
            .isActive(userDO.getIsActivate())
            .level(userDO.getLevel() == null ? 0L : userDO.getLevel().longValue())
            .experience(userDO.getExperience() == null ? 0L : userDO.getExperience().longValue())
            .avatar(userDO.getAvatar() == null ? "" : userDO.getAvatar())
            .build();
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateUserRole(Integer userId, UpdateUserRoleDTO dto) {
        if (userId == null) {
            return;
        }
        if (RoleEnum.NORMAL.getCode().equals(UserContext.getLoginUser().getRole())) {
            throw new ServiceException("没有权限！");
        }

        if (userId == 1) {
            throw new ServiceException("不能修改超级管理员权限！");
        }
        this.userRoleService.lambdaUpdate().eq(UserRoleDO::getUserId, userId).remove();
        String role = dto.getRole();
        List<Integer> roleIds = RoleEnum.getRoleIds(role);
        if (CollUtil.isEmpty(roleIds)) {
            throw new ServiceException("没有该角色");
        }
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setUserId(userId);
        userRoleDO.setRoleId(roleIds.getFirst());
        this.userRoleService.save(userRoleDO);
    }

    public void updateUserPassword(Integer userId, AdminUpdatePasswordDTO dto) {
        if (userId == null) {
            return;
        }
        this.lambdaUpdate()
            .eq(UserDO::getId, userId)
            .set(UserDO::getPassword, PasswordUtil.encode(dto.getNewPassword()))
            .update();
    }

    public PageResult<WikiListVO> getWikis(String pageNum, String pageSize, String sortBy, String order, List<String> status, String q) {
        Integer userId = UserContext.getUserId();
        PageResult<WikiListVO> thinkTankList = this.thinkTankService.getThinkTankList(pageNum, pageSize, sortBy, order, status, String.valueOf(userId), q);
        Pagination pagination = thinkTankList.getPagination();
        if (CollUtil.isNotEmpty(thinkTankList.getList())) {
            return new PageResult<>(thinkTankList.getList(), pagination.getTotal(), pagination.getCurrentPage(), pagination.getPages());
        }
        return new PageResult<>(new ArrayList<>(), pagination.getTotal(), pagination.getCurrentPage(), pagination.getPages());
    }

    public void submitWikiApproval(String id) {
        if (id == null) {
            throw new ServiceException("词条id不能为空");
        }
        if (this.thinkTankService.getById(id) == null) {
            throw new ServiceException("词条不存在");
        }
        this.thinkTankService.lambdaUpdate()
            .eq(ThinkTankDO::getId, id)
            .set(ThinkTankDO::getState, WikiStateEnum.PENDING_REVIEW.getCode())
            .update();
    }
}
