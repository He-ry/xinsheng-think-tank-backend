package com.learn.service.userrole;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.models.entity.RoleDO;
import com.learn.models.entity.UserRoleDO;
import com.learn.models.mapper.RoleMapper;
import com.learn.models.mapper.UserRoleMapper;
import com.learn.service.userrole.UserRoleService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserRoleServiceImpl
extends ServiceImpl<UserRoleMapper, UserRoleDO>
implements UserRoleService {
    private static final Logger log = LoggerFactory.getLogger(UserRoleServiceImpl.class);
    @Resource
    private RoleMapper roleMapper;

    public UserRoleDO insertUserRole(Integer userId, Integer roleId) {
        List<UserRoleDO> list = this.lambdaQuery()
            .eq(UserRoleDO::getUserId, userId)
            .eq(UserRoleDO::getRoleId, roleId)
            .list();
        if (CollUtil.isNotEmpty(list)) {
            log.info("用户已存在该角色:{}", userId);
            return list.getFirst();
        }
        UserRoleDO userRole = UserRoleDO.builder().userId(userId).roleId(roleId).build();
        this.save(userRole);
        return userRole;
    }

    public List<RoleDO> getUserRole(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        List<UserRoleDO> list = this.lambdaQuery().eq(UserRoleDO::getUserId, userId).list();
        List<RoleDO> roleDOS = this.roleMapper.selectList(RoleDO::getId, list.stream().map(UserRoleDO::getRoleId).toList());
        if (CollUtil.isEmpty(roleDOS)) {
            return new ArrayList<>();
        }
        return roleDOS;
    }

    public HashMap<Integer, List<RoleDO>> getUserRoleByUserIds(List<Integer> userIds) {
        HashMap<Integer, List<RoleDO>> result = new HashMap<>();
        if (CollUtil.isEmpty(userIds)) {
            return result;
        }
        List<UserRoleDO> userRoleList = this.lambdaQuery().in(UserRoleDO::getUserId, userIds).list();
        if (CollUtil.isEmpty(userRoleList)) {
            return result;
        }
        List<Integer> roleIds = userRoleList.stream().map(UserRoleDO::getRoleId).distinct().toList();
        List<RoleDO> roleList = this.roleMapper.selectList(RoleDO::getId, roleIds);
        if (CollUtil.isEmpty(roleList)) {
            return result;
        }
        Map<Integer, RoleDO> roleMap = roleList.stream().collect(Collectors.toMap(RoleDO::getId, r -> r));
        for (UserRoleDO ur : userRoleList) {
            RoleDO role = roleMap.get(ur.getRoleId());
            if (role == null) continue;
            result.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(role);
        }
        return result;
    }
}
