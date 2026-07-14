package com.learn.models.mapper;

import com.learn.config.mybatis.BaseMapperX;
import com.learn.models.entity.UserRoleDO;
import org.apache.ibatis.annotations.Mapper;

import lombok.Data;
@Mapper
public interface UserRoleMapper
extends BaseMapperX<UserRoleDO> {
}
