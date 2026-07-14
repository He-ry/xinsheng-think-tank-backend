package com.learn.config.mybatis;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.base.MPJBaseMapper;
import java.util.Collection;
import java.util.List;

import lombok.Data;
public interface BaseMapperX<T>
extends MPJBaseMapper<T> {
    default public T selectOne(String field, Object value) {
        return (T)this.selectOne((Wrapper)new QueryWrapper().eq((Object)field, value));
    }

    default public T selectOne(SFunction<T, ?> field, Object value) {
        return (T)this.selectOne((Wrapper)new LambdaQueryWrapper().eq(field, value));
    }

    default public T selectOne(String field1, Object value1, String field2, Object value2) {
        return (T)this.selectOne((Wrapper)((QueryWrapper)new QueryWrapper().eq((Object)field1, value1)).eq((Object)field2, value2));
    }

    default public T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return (T)this.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(field1, value1)).eq(field2, value2));
    }

    default public T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, ?> field3, Object value3) {
        return (T)this.selectOne((Wrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(field1, value1)).eq(field2, value2)).eq(field3, value3));
    }

    default public T selectFirstOne(SFunction<T, ?> field, Object value) {
        List list = this.selectList((Wrapper)new LambdaQueryWrapper().eq(field, value));
        return (T)CollUtil.getFirst((Iterable)list);
    }

    default public T selectFirstOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        List list = this.selectList((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(field1, value1)).eq(field2, value2));
        return (T)CollUtil.getFirst((Iterable)list);
    }

    default public T selectFirstOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, ?> field3, Object value3) {
        List list = this.selectList((Wrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(field1, value1)).eq(field2, value2)).eq(field3, value3));
        return (T)CollUtil.getFirst((Iterable)list);
    }

    default public Long selectCount() {
        return this.selectCount((Wrapper)new QueryWrapper());
    }

    default public Long selectCount(String field, Object value) {
        return this.selectCount((Wrapper)new QueryWrapper().eq((Object)field, value));
    }

    default public Long selectCount(SFunction<T, ?> field, Object value) {
        return this.selectCount((Wrapper)new LambdaQueryWrapper().eq(field, value));
    }

    default public List<T> selectList() {
        return this.selectList((Wrapper)new QueryWrapper());
    }

    default public List<T> selectList(String field, Object value) {
        return this.selectList((Wrapper)new QueryWrapper().eq((Object)field, value));
    }

    default public List<T> selectList(SFunction<T, ?> field, Object value) {
        return this.selectList((Wrapper)new LambdaQueryWrapper().eq(field, value));
    }

    default public List<T> selectList(String field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return new java.util.ArrayList<>();
        }
        return this.selectList((Wrapper)new QueryWrapper().in((Object)field, values));
    }

    default public List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return new java.util.ArrayList<>();
        }
        return this.selectList((Wrapper)new LambdaQueryWrapper().in(field, values));
    }

    default public List<T> selectList(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return this.selectList((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(field1, value1)).eq(field2, value2));
    }

    default public int updateBatch(T update) {
        return this.update(update, (Wrapper)new QueryWrapper());
    }

    default public Boolean updateBatch(Collection<T> entities) {
        return Db.updateBatchById(entities);
    }

    default public Boolean updateBatch(Collection<T> entities, int size) {
        return Db.updateBatchById(entities, (int)size);
    }

    default public int delete(String field, String value) {
        return this.delete((Wrapper)new QueryWrapper().eq((Object)field, (Object)value));
    }

    default public int delete(SFunction<T, ?> field, Object value) {
        return this.delete((Wrapper)new LambdaQueryWrapper().eq(field, value));
    }

    default public int deleteBatch(SFunction<T, ?> field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return 0;
        }
        return this.delete((Wrapper)new LambdaQueryWrapper().in(field, values));
    }
}
