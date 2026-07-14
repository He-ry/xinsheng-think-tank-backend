package com.learn.service.system;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.models.entity.SystemDO;
import com.learn.models.enums.SystemKeyEnum;
import com.learn.models.mapper.SystemMapper;
import com.learn.models.mapper.ThinkTankMapper;
import com.learn.service.system.SystemService;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class SystemServiceImpl
extends ServiceImpl<SystemMapper, SystemDO>
implements SystemService {
    @Resource
    private ThinkTankMapper thinkTankMapper;
    @Resource
    private SystemMapper systemMapper;

    public long count() {
        return this.thinkTankMapper.selectCount();
    }


    public HashMap<String, Object> getVisitorCount() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("today", 0L);
        map.put("total", 0L);
        List list = ((LambdaQueryChainWrapper)this.lambdaQuery().in(SystemDO::getSysKey, List.of(SystemKeyEnum.TODAY_VIEWCOUNT.getKey(), SystemKeyEnum.TOTAL_VIEWCOUNT.getKey()))).list();
        if (CollUtil.isNotEmpty((Collection)list)) {
            list.forEach(item -> {
                if (SystemKeyEnum.TODAY_VIEWCOUNT.getKey().equals(item.getSysKey())) {
                    map.put("today", Long.parseLong(item.getValue()));
                }
                if (SystemKeyEnum.TOTAL_VIEWCOUNT.getKey().equals(item.getSysKey())) {
                    map.put("total", Long.parseLong(item.getValue()) + Long.parseLong(map.get("today").toString()));
                }
            });
        }
        return map;
    }

    @Transactional
    public void increaseVisitor() {
        ((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)this.lambdaUpdate().eq(SystemDO::getSysKey, (Object)SystemKeyEnum.TODAY_VIEWCOUNT.getKey())).setSql("value = CAST(value AS UNSIGNED) + 1", new Object[0])).update();
    }

    @Transactional(rollbackFor={Exception.class})
    public void archiveTodayVisitor() {
        this.ensureKeyExists(SystemKeyEnum.TOTAL_VIEWCOUNT.getKey());
        this.ensureKeyExists(SystemKeyEnum.TODAY_VIEWCOUNT.getKey());
        SystemDO todayViewCount = (SystemDO)this.systemMapper.selectFirstOne(SystemDO::getSysKey, (Object)SystemKeyEnum.TODAY_VIEWCOUNT.getKey());
        SystemDO totalViewCount = (SystemDO)this.systemMapper.selectFirstOne(SystemDO::getSysKey, (Object)SystemKeyEnum.TOTAL_VIEWCOUNT.getKey());
        ((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)this.lambdaUpdate().eq(SystemDO::getSysKey, (Object)SystemKeyEnum.TOTAL_VIEWCOUNT.getKey())).set(SystemDO::getValue, (Object)("" + (Long.parseLong(totalViewCount.getValue()) + Long.parseLong(todayViewCount.getValue()))))).update();
        ((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)this.lambdaUpdate().eq(SystemDO::getSysKey, (Object)SystemKeyEnum.TODAY_VIEWCOUNT.getKey())).set(SystemDO::getValue, (Object)"0")).update();
    }


    public void ensureKeyExists(String key) {
        boolean exists = ((LambdaQueryChainWrapper)this.lambdaQuery().eq(SystemDO::getSysKey, (Object)key)).exists();
        if (!exists) {
            SystemDO systemDO = new SystemDO();
            systemDO.setSysKey(key);
            systemDO.setValue("0");
            systemDO.setDescription("");
            this.systemMapper.insert((Object)systemDO);
        }
    }
}
