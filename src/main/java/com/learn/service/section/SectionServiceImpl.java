package com.learn.service.section;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.domain.vo.RecommendVO;
import com.learn.domain.vo.SearchVO;
import com.learn.models.entity.SectionDO;
import com.learn.models.entity.ThinkTankDO;
import com.learn.models.enums.WikiStateEnum;
import com.learn.models.mapper.SectionMapper;
import com.learn.models.mapper.ThinkTankMapper;
import com.learn.service.section.SectionService;
import com.learn.service.thinktank.ThinkTankService;
import jakarta.annotation.Resource;
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
    @Resource
    private ThinkTankMapper thinkTankMapper;

    public SearchVO fulltextSearch(String keyword, Integer pageNum, Integer pageSize) {
        long offset = (long) (pageNum - 1) * pageSize;
        List<ThinkTankDO> records = thinkTankMapper.fulltextSearch(keyword, offset, pageSize);
        // Fallback: if FULLTEXT returns nothing (short query), use LIKE search
        if (records == null || records.isEmpty()) {
            return searchSuggest(keyword, pageNum, pageSize);
        }
        long total = thinkTankMapper.fulltextSearchCount(keyword);
        List<SearchVO.SearchItemVO> items = BeanUtil.copyToList(records, SearchVO.SearchItemVO.class);
        boolean hasPrev = pageNum > 1;
        boolean hasNext = offset + pageSize < total;
        return SearchVO.builder()
                .total(total)
                .search(items)
                .pagination(new SearchVO.PaginationVO(pageNum, total, hasPrev, hasNext))
                .build();
    }

    public SearchVO searchSuggest(String title, Integer pageNum, Integer pageSize) {
        Page<ThinkTankDO> page = this.thinkTankService.lambdaQuery()
            .eq(ThinkTankDO::getState, WikiStateEnum.PUBLISHED.getCode())
            .like(StrUtil.isNotEmpty(title), ThinkTankDO::getTitle, title)
            .or()
            .like(StrUtil.isNotEmpty(title), ThinkTankDO::getSummary, title)
            .select(ThinkTankDO::getTitle, ThinkTankDO::getCanonicalUrl)
            .page(Page.of(pageNum.longValue(), pageSize.longValue()));
        List<ThinkTankDO> records = page.getRecords();
        List<SearchVO.SearchItemVO> searchItemVOS = BeanUtil.copyToList(records, SearchVO.SearchItemVO.class);
        if (CollUtil.isEmpty(records)) {
            return SearchVO.builder()
                .total(page.getTotal())
                .search(searchItemVOS)
                .pagination(new SearchVO.PaginationVO(pageNum, page.getTotal(), false, false))
                .build();
        }
        boolean hasPrev = pageNum > 1;
        boolean hasNext = pageNum.longValue() * pageSize.longValue() < page.getTotal();
        return SearchVO.builder()
            .total(page.getTotal())
            .search(searchItemVOS)
            .pagination(new SearchVO.PaginationVO(pageNum, page.getTotal(), hasPrev, hasNext))
            .build();
    }

    public List<RecommendVO> random(Integer num) {
        long count = this.thinkTankService.lambdaQuery()
            .eq(ThinkTankDO::getState, WikiStateEnum.PUBLISHED.getCode())
            .count();
        if (count <= 5L) {
            List<ThinkTankDO> list = this.thinkTankService.lambdaQuery()
                .eq(ThinkTankDO::getState, WikiStateEnum.PUBLISHED.getCode())
                .list();
            return BeanUtil.copyToList(list, RecommendVO.class);
        }
        long offset = ThreadLocalRandom.current().nextLong(0L, count - 5L);
        List<ThinkTankDO> list = this.thinkTankService.lambdaQuery()
            .eq(ThinkTankDO::getState, WikiStateEnum.PUBLISHED.getCode())
            .last("LIMIT " + offset + ", 5")
            .list();
        return BeanUtil.copyToList(list, RecommendVO.class);
    }

    public void deleteSectionByThinkTankId(String id) {
        QueryWrapper<SectionDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("think_tank_id", id);
        this.sectionMapper.delete(queryWrapper);
    }
}
