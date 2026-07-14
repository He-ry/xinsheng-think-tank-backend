package com.learn.config.mybatis;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import java.util.Collection;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

import lombok.Data;
@Data
public class LambdaQueryWrapperX<T>
extends LambdaQueryWrapper<T> {
    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        if (StringUtils.hasText((String)val)) {
            return (LambdaQueryWrapperX)super.like(column, (Object)val);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (ObjectUtil.isAllNotEmpty((Object[])new Object[]{values}) && !ArrayUtil.isEmpty(values)) {
            return (LambdaQueryWrapperX)super.in(column, values);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Object ... values) {
        if (ObjectUtil.isAllNotEmpty((Object[])values) && !ArrayUtil.isEmpty((Object[])values)) {
            return (LambdaQueryWrapperX)super.in(column, values);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty((Object)val)) {
            return (LambdaQueryWrapperX)super.eq(column, val);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty((Object)val)) {
            return (LambdaQueryWrapperX)super.ne(column, val);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX)super.gt(column, val);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX)super.ge(column, val);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX)super.lt(column, val);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX)super.le(column, val);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (LambdaQueryWrapperX)super.between(column, val1, val2);
        }


        if (val1 != null) {
            return (LambdaQueryWrapperX)this.ge(column, val1);
        }


        if (val2 != null) {
            return (LambdaQueryWrapperX)this.le(column, val2);
        }
        return this;
    }


    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        Object val1 = ArrayUtils.get((Object[])values, (int)0);
        Object val2 = ArrayUtils.get((Object[])values, (int)1);
        return this.betweenIfPresent(column, val1, val2);
    }


    public LambdaQueryWrapperX<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        super.eq(condition, column, val);
        return this;
    }


    public LambdaQueryWrapperX<T> eq(SFunction<T, ?> column, Object val) {
        super.eq(column, val);
        return this;
    }


    public LambdaQueryWrapperX<T> orderByDesc(SFunction<T, ?> column) {
        super.orderByDesc(true, column);
        return this;
    }


    public LambdaQueryWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }


    public LambdaQueryWrapperX<T> in(SFunction<T, ?> column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }
}
