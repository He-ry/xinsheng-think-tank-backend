package com.learn.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.models.entity.SystemDO;
import java.util.HashMap;

import lombok.Data;
public interface SystemService
extends IService<SystemDO> {
    public long count();

    public HashMap<String, Object> getVisitorCount();

    public void increaseVisitor();

    public void ensureKeyExists(String var1);

    public void archiveTodayVisitor();
}
