package com.learn.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learn.domain.pojo.SortablePageParam;
import com.learn.domain.pojo.SortingField;
import java.util.ArrayList;
import java.util.Collection;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import lombok.Data;
/*
 * Exception performing whole class analysis ignored.
 */
@Data
public class MyBatisUtils {
    private static final String MYSQL_ESCAPE_CHARACTER = "`";

    public static <T> Page<T> buildPage(SortablePageParam pageParam) {
        return MyBatisUtils.buildPage((SortablePageParam)pageParam, null);
    }

    public static <T> Page<T> buildPage(SortablePageParam pageParam, Collection<SortingField> sortingFields) {
        Page page = new Page((long)pageParam.getPage().intValue(), (long)pageParam.getPerPage().intValue());
        if (CollUtil.isNotEmpty(sortingFields)) {
            for (SortingField sortingField : sortingFields) {
                page.addOrder(new OrderItem[]{new OrderItem().setAsc("asc".equals(sortingField.getOrder())).setColumn(StrUtil.toUnderlineCase((CharSequence)sortingField.getField()))});
            }
        }
        return page;
    }

    public static <T> void addOrder(Wrapper<T> wrapper, Collection<SortingField> sortingFields) {
        if (CollUtil.isEmpty(sortingFields)) {
            return;
        }
        QueryWrapper query = (QueryWrapper)wrapper;
        for (SortingField sortingField : sortingFields) {
            query.orderBy(true, "asc".equals(sortingField.getOrder()), (Object)StrUtil.toUnderlineCase((CharSequence)sortingField.getField()));
        }
    }

    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        ArrayList<InnerInterceptor> inners = new ArrayList<InnerInterceptor>(interceptor.getInterceptors());
        inners.add(index, inner);
        interceptor.setInterceptors(inners);
    }

    public static String getTableName(Table table) {
        String tableName = table.getName();
        if (tableName.startsWith("`") && tableName.endsWith("`")) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            tableName = tableAlias.getName();
        }
        return new Column(tableName + "." + column);
    }
}
