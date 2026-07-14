package com.learn.service.userrole;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.models.entity.RoleDO;
import com.learn.models.entity.UserRoleDO;
import java.util.HashMap;
import java.util.List;

import lombok.Data;
public interface UserRoleService
extends IService<UserRoleDO> {
    public UserRoleDO insertUserRole(Integer var1, Integer var2);

    public List<RoleDO> getUserRole(Integer var1);

    public HashMap<Integer, List<RoleDO>> getUserRoleByUserIds(List<Integer> var1);
}
