package com.learn.service.section;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.domain.vo.RecommendVO;
import com.learn.domain.vo.SearchVO;
import com.learn.models.entity.SectionDO;
import java.util.List;

import lombok.Data;
public interface SectionService
extends IService<SectionDO> {
    public SearchVO searchSuggest(String var1, Integer var2, Integer var3);

    public SearchVO fulltextSearch(String var1, Integer var2, Integer var3);

    public List<RecommendVO> random(Integer var1);

    public void deleteSectionByThinkTankId(String var1);
}
