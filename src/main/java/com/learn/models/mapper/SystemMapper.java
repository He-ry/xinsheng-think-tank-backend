package com.learn.models.mapper;

import com.learn.config.mybatis.BaseMapperX;
import com.learn.models.entity.SystemDO;
import org.apache.ibatis.annotations.Mapper;

import lombok.Data;
@Mapper
public interface SystemMapper
extends BaseMapperX<SystemDO> {
}
