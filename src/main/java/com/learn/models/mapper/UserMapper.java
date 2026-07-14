package com.learn.models.mapper;

import com.learn.config.mybatis.BaseMapperX;
import com.learn.models.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

import lombok.Data;
@Mapper
public interface UserMapper
extends BaseMapperX<UserDO> {
}
