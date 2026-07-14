package com.learn.service.section;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.domain.vo.RecommendVO;
import com.learn.domain.vo.SearchVO;
import com.learn.models.entity.SectionDO;
import com.learn.models.entity.ThinkTankDO;
import com.learn.models.enums.WikiStateEnum;
import com.learn.models.mapper.SectionMapper;
import com.learn.service.section.SectionService;
import com.learn.service.thinktank.ThinkTankService;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class SectionServiceImpl
extends ServiceImpl<SectionMapper, SectionDO>
implements SectionService {
    @Resource
    private ThinkTankService thinkTankService;
    @Resource
    private SectionMapper sectionMapper;

    public SearchVO searchSuggest(String title, Integer pageNum, Integer pageSize) {
        Page page = (Page)((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)this.thinkTankService.lambdaQuery().eq(ThinkTankDO::getState, (Object)WikiStateEnum.PUBLISHED.getCode())).like(StrUtil.isNotEmpty((CharSequence)title), ThinkTankDO::getTitle, (Object)title)).or()).like(StrUtil.isNotEmpty((CharSequence)title), ThinkTankDO::getSummary, (Object)title)).select(new SFunction[]{ThinkTankDO::getTitle, ThinkTankDO::getCanonicalUrl}).page((IPage)Page.of((long)pageNum.intValue(), (long)pageSize.intValue()));
        List records = page.getRecords();
        List searchItemVOS = BeanUtil.copyToList((Collection)records, SearchVO.SearchItemVO.class);
        if (CollUtil.isEmpty((Collection)records)) {
            return SearchVO.builder().total(Long.valueOf(page.getTotal())).search(searchItemVOS).pagination(new SearchVO.PaginationVO(pageNum, Long.valueOf(page.getTotal()), Boolean.valueOf(false), Boolean.valueOf(false))).build();
        }
        boolean hasPrev = pageNum > 1;
        boolean hasNext = (long)pageNum.intValue() * (long)pageSize.intValue() < page.getTotal();
        return SearchVO.builder().total(Long.valueOf(page.getTotal())).search(searchItemVOS).pagination(new SearchVO.PaginationVO(pageNum, Long.valueOf(page.getTotal()), Boolean.valueOf(hasPrev), Boolean.valueOf(hasNext))).build();
    }


    public List<RecommendVO> random(Integer num) {
        long count = ((LambdaQueryChainWrapper)this.thinkTankService.lambdaQuery().eq(ThinkTankDO::getState, (Object)WikiStateEnum.PUBLISHED.getCode())).count();
        if (count <= 5L) {
            List list = ((LambdaQueryChainWrapper)this.thinkTankService.lambdaQuery().eq(ThinkTankDO::getState, (Object)WikiStateEnum.PUBLISHED.getCode())).list();
            return BeanUtil.copyToList((Collection)list, RecommendVO.class);
        }
        long offset = ThreadLocalRandom.current().nextLong(0L, count - 5L);
        List list = ((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)this.thinkTankService.lambdaQuery().eq(ThinkTankDO::getState, (Object)WikiStateEnum.PUBLISHED.getCode())).last("LIMIT " + offset + ", 5")).list();
        return BeanUtil.copyToList((Collection)list, RecommendVO.class);
    }


    public void deleteSectionByThinkTankId(String id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq((Object)"think_tank_id", (Object)id);
        this.sectionMapper.delete((Wrapper)queryWrapper);
    }
}
