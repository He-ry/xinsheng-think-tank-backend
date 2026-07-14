package com.learn.service.userrole;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.models.entity.RoleDO;
import com.learn.models.entity.UserRoleDO;
import com.learn.models.mapper.RoleMapper;
import com.learn.models.mapper.UserRoleMapper;
import com.learn.service.userrole.UserRoleService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
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
        List list = ((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)this.lambdaQuery().eq(UserRoleDO::getUserId, (Object)userId)).eq(UserRoleDO::getRoleId, (Object)roleId)).list();
        if (CollUtil.isNotEmpty((Collection)list)) {
            log.info("用户已存在该角色:{}", (Object)userId);
            return (UserRoleDO)list.getFirst();
        }
        UserRoleDO userRole = UserRoleDO.builder().userId(userId).roleId(roleId).build();
        this.save((Object)userRole);
        return userRole;
    }


    public List<RoleDO> getUserRole(Integer userId) {
        if (userId == null) {
            return CollUtil.newArrayList((Object[])new RoleDO[0]);
        }
        List list = ((LambdaQueryChainWrapper)this.lambdaQuery().eq(UserRoleDO::getUserId, (Object)userId)).list();
        List roleDOS = this.roleMapper.selectList(RoleDO::getId, list.stream().map(UserRoleDO::getRoleId).toList());
        if (CollUtil.isEmpty((Collection)roleDOS)) {
            return CollUtil.newArrayList((Object[])new RoleDO[0]);
        }
        return roleDOS;
    }

    public HashMap<Integer, List<RoleDO>> getUserRoleByUserIds(List<Integer> userIds) {
        HashMap<Integer, List<RoleDO>> result = new HashMap<Integer, List<RoleDO>>();
        if (CollUtil.isEmpty(userIds)) {
            return result;
        }
        List userRoleList = ((LambdaQueryChainWrapper)this.lambdaQuery().in(UserRoleDO::getUserId, userIds)).list();
        if (CollUtil.isEmpty((Collection)userRoleList)) {
            return result;
        }
        List<Integer> roleIds = userRoleList.stream().map(UserRoleDO::getRoleId).distinct().toList();
        List roleList = this.roleMapper.selectList(RoleDO::getId, roleIds);
        if (CollUtil.isEmpty((Collection)roleList)) {
            return result;
        }
        Map<Integer, RoleDO> roleMap = roleList.stream().collect(Collectors.toMap(RoleDO::getId, r -> r));
        for (UserRoleDO ur : userRoleList) {
            RoleDO role = roleMap.get(ur.getRoleId());
            if (role == null) continue;
            result.computeIfAbsent(ur.getUserId(), k -> new ArrayList()).add(role);
        }
        return result;
    }
}
