package com.learn.service.role;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.models.entity.RoleDO;
import com.learn.models.mapper.RoleMapper;
import com.learn.service.role.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RoleServiceImpl
extends ServiceImpl<RoleMapper, RoleDO>
implements RoleService {
    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
}
