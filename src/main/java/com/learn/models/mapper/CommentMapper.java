package com.learn.models.mapper;

import com.learn.config.mybatis.BaseMapperX;
import com.learn.models.entity.CommentDO;
import org.apache.ibatis.annotations.Mapper;

import lombok.Data;
@Mapper
public interface CommentMapper
extends BaseMapperX<CommentDO> {
}
