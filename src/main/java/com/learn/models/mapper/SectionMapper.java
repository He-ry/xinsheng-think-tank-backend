package com.learn.models.mapper;

import com.learn.config.mybatis.BaseMapperX;
import com.learn.models.entity.SectionDO;
import org.apache.ibatis.annotations.Mapper;

import lombok.Data;
@Mapper
public interface SectionMapper
extends BaseMapperX<SectionDO> {
}
