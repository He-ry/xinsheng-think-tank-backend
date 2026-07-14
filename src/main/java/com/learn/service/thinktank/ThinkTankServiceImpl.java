package com.learn.service.thinktank;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.domain.dto.ThinkTankApprovalDTO;
import com.learn.domain.dto.WikiSaveDTO;
import com.learn.domain.pojo.PageResult;
import com.learn.domain.pojo.UserContext;
import com.learn.domain.vo.WikiContentRemoveVO;
import com.learn.domain.vo.WikiContentVO;
import com.learn.domain.vo.WikiListVO;
import com.learn.exception.ServiceException;
import com.learn.models.bo.CitationBO;
import com.learn.models.bo.ContentBO;
import com.learn.models.bo.RelatedEntryBO;
import com.learn.models.bo.SectionBO;
import com.learn.models.entity.SectionDO;
import com.learn.models.entity.ThinkTankDO;
import com.learn.models.entity.UserDO;
import com.learn.models.enums.WikiStateEnum;
import com.learn.models.mapper.ThinkTankMapper;
import com.learn.service.section.SectionService;
import com.learn.service.thinktank.ThinkTankService;
import com.learn.service.user.UserService;
import com.learn.utils.SectionConvertUtil;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ThinkTankServiceImpl
extends ServiceImpl<ThinkTankMapper, ThinkTankDO>
implements ThinkTankService {
    private static final Logger log = LoggerFactory.getLogger(ThinkTankServiceImpl.class);
    @Resource
    private ThinkTankMapper thinkTankMapper;
    @Lazy
    @Resource
    private SectionService sectionService;
    @Lazy
    @Resource
    private UserService userService;

    @Transactional(rollbackFor={Exception.class})
    public String createThinkTank(WikiSaveDTO dto) {
        String userIdStr = UserContext.getUserIdStr();
        ThinkTankDO build = ThinkTankDO.builder().id(dto.getId()).title(dto.getWikiEntryInfo().getTitle()).canonicalUrl("/page/" + dto.getWikiEntryInfo().getTitle()).summary(dto.getWikiContent().getSummary()).creator(userIdStr).createAt(Long.valueOf(Instant.now().getEpochSecond())).updateAt(Long.valueOf(Instant.now().getEpochSecond())).view(Long.valueOf(0L)).totalWords(SectionConvertUtil.getTotalWords((String)dto.getWikiEntryInfo().getTitle(), (List)dto.getWikiContent().getSections())).chaptersNum(JSONUtil.toJsonStr((Object)SectionConvertUtil.getChapterNumber((List)dto.getWikiContent().getSections()))).citations(JSONUtil.toJsonStr((Object)dto.getCitations())).build();
        if (CollUtil.isNotEmpty((Collection)dto.getRelatedEntries())) {
            build.setRelatedEntries(JSONUtil.toJsonStr((Object)dto.getRelatedEntries()));
        }
        ArrayList checkSectionDOS = new ArrayList();
        if (StrUtil.isEmpty((CharSequence)dto.getId()) || "0".equals(dto.getId())) {
            Long count = ((LambdaQueryChainWrapper)this.lambdaQuery().select(new SFunction[]{ThinkTankDO::getCanonicalUrl}).eq(ThinkTankDO::getCanonicalUrl, (Object)build.getCanonicalUrl())).count();
            if (count > 0L) {
                throw new ServiceException("词条已存在");
            }
            build.setState(WikiStateEnum.PENDING_REVIEW.getCode());
            build.setStateMessage("");
            build.setId(null);
            this.thinkTankMapper.insert((Object)build);
            List sections = dto.getWikiContent().getSections();
            List sectionDOS = SectionConvertUtil.convertToDO((String)build.getId(), (List)sections, (Boolean)false);
            this.sectionService.saveBatch((Collection)sectionDOS);
            checkSectionDOS.addAll(sectionDOS);
        } else {
            ThinkTankDO thinkTankDO = (ThinkTankDO)this.thinkTankMapper.selectById((Serializable)((Object)build.getId()));
            if (thinkTankDO == null) {
                throw new ServiceException("词条不存在");
            }
            build.setView(Long.valueOf(thinkTankDO.getView() + 1L));
            build.setCreator(thinkTankDO.getCreator());
            this.thinkTankMapper.insertOrUpdate((Object)build);
            List sections = dto.getWikiContent().getSections();
            List sectionDOS = SectionConvertUtil.convertToDO((String)build.getId(), (List)sections, (Boolean)true);
            this.sectionService.saveOrUpdateBatch((Collection)sectionDOS);
            if (!thinkTankDO.getTitle().equals(dto.getWikiEntryInfo().getTitle())) {
                log.info("更新相关引用的词条标题内容");
                List list = ((LambdaQueryChainWrapper)this.lambdaQuery().select(new SFunction[0]).like(ThinkTankDO::getRelatedEntries, (Object)thinkTankDO.getId())).list();
                for (ThinkTankDO thinkTankDO1 : list) {
                    String relatedEntries = thinkTankDO1.getRelatedEntries();
                    List relatedEntryBOs = JSONUtil.toList((String)relatedEntries, RelatedEntryBO.class);
                    relatedEntryBOs.removeIf(relatedEntryBO -> relatedEntryBO.getId().equals(thinkTankDO.getId()));
                    relatedEntryBOs.add(new RelatedEntryBO(thinkTankDO.getId(), build.getTitle(), build.getCanonicalUrl()));
                    thinkTankDO1.setRelatedEntries(JSONUtil.toJsonStr((Object)relatedEntryBOs));
                    this.thinkTankMapper.updateById((Object)thinkTankDO1);
                }
            } else {
                log.info("标题相同不需要更新相关内容");
            }
            checkSectionDOS.addAll(sectionDOS);
        }
        this.addRelateEntries(build, checkSectionDOS);
        return build.getId();
    }


    private void addRelateEntries(ThinkTankDO build, List<SectionDO> checkSectionDOS) {
        new Thread(() -> {
            log.info("开始更新相关引用的词条内容....");
            List list = ((LambdaQueryChainWrapper)this.lambdaQuery().select(new SFunction[]{ThinkTankDO::getId, ThinkTankDO::getTitle, ThinkTankDO::getCanonicalUrl}).ne(StrUtil.isNotBlank((CharSequence)build.getId()), ThinkTankDO::getId, (Object)build.getId())).list();
            if (CollUtil.isNotEmpty((Collection)list) && CollUtil.isNotEmpty((Collection)checkSectionDOS)) {
                String allContent = checkSectionDOS.stream().map(SectionDO::getContent).collect(Collectors.joining());
                HashSet<RelatedEntryBO> set = new HashSet<RelatedEntryBO>();
                for (ThinkTankDO t : list) {
                    if (!allContent.contains(t.getTitle())) continue;
                    set.add(new RelatedEntryBO(t.getId(), t.getTitle(), t.getCanonicalUrl()));
                }
                ((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)this.lambdaUpdate().set(ThinkTankDO::getRelatedEntries, (Object)JSONUtil.toJsonStr(set))).eq(ThinkTankDO::getId, (Object)build.getId())).update();
            }
        }).start();
    }

    @Transactional(rollbackFor={Exception.class})
    public String aiCreateThinkTank(WikiSaveDTO dto) {
        ThinkTankDO build = ThinkTankDO.builder().id(dto.getId()).title(dto.getWikiEntryInfo().getTitle()).canonicalUrl("/page/" + dto.getWikiEntryInfo().getTitle()).summary(dto.getWikiContent().getSummary()).createAt(Long.valueOf(Instant.now().getEpochSecond())).updateAt(Long.valueOf(Instant.now().getEpochSecond())).view(Long.valueOf(0L)).totalWords(SectionConvertUtil.getTotalWords((String)dto.getWikiEntryInfo().getTitle(), (List)dto.getWikiContent().getSections())).chaptersNum(JSONUtil.toJsonStr((Object)SectionConvertUtil.getChapterNumber((List)dto.getWikiContent().getSections()))).citations(JSONUtil.toJsonStr((Object)dto.getCitations())).build();
        ThinkTankDO thinkTankDO = (ThinkTankDO)this.thinkTankMapper.selectById((Serializable)((Object)build.getId()));
        if (thinkTankDO == null) {
            throw new ServiceException("词条不存在");
        }
        build.setView(Long.valueOf(thinkTankDO.getView() + 1L));
        this.thinkTankMapper.insertOrUpdate((Object)build);
        List sections = dto.getWikiContent().getSections();
        List sectionDOS = SectionConvertUtil.convertToDO((String)build.getId(), (List)sections, (Boolean)false);
        this.sectionService.saveOrUpdateBatch((Collection)sectionDOS);
        if (!thinkTankDO.getTitle().equals(dto.getWikiEntryInfo().getTitle())) {
            log.info("更新相关引用的词条标题内容");
            List list = ((LambdaQueryChainWrapper)this.lambdaQuery().select(new SFunction[0]).like(ThinkTankDO::getRelatedEntries, (Object)thinkTankDO.getId())).list();
            for (ThinkTankDO thinkTankDO1 : list) {
                String relatedEntries = thinkTankDO1.getRelatedEntries();
                List relatedEntryBOs = JSONUtil.toList((String)relatedEntries, RelatedEntryBO.class);
                relatedEntryBOs.removeIf(relatedEntryBO -> relatedEntryBO.getId().equals(thinkTankDO.getId()));
                relatedEntryBOs.add(new RelatedEntryBO(thinkTankDO.getId(), build.getTitle(), build.getCanonicalUrl()));
                thinkTankDO1.setRelatedEntries(JSONUtil.toJsonStr((Object)relatedEntryBOs));
                this.thinkTankMapper.updateById((Object)thinkTankDO1);
            }
        } else {
            log.info("标题相同不需要更新相关内容");
        }
        this.addRelateEntries(build, sectionDOS);
        return build.getId();
    }

    @Transactional(rollbackFor={Exception.class})
    public WikiContentRemoveVO deleteThinkTank(String id) {
        ThinkTankDO thinkTankDO = (ThinkTankDO)this.getById((Serializable)((Object)id));
        if (thinkTankDO == null) {
            throw new ServiceException("词条不存在");
        }
        List list = ((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)this.lambdaQuery().select(new SFunction[]{ThinkTankDO::getRelatedEntries}).like(ThinkTankDO::getRelatedEntries, (Object)thinkTankDO.getId())).like(ThinkTankDO::getRelatedEntries, (Object)thinkTankDO.getCanonicalUrl())).list();
        if (CollUtil.isNotEmpty((Collection)list)) {
            log.info("相关词条内容为空");
        }
        List<String> removeList = list.stream().map(ThinkTankDO::getId).toList();
        list.forEach(thinkTankDO1 -> {
            List relatedEntries = JSONUtil.toList((String)thinkTankDO1.getRelatedEntries(), RelatedEntryBO.class);
            relatedEntries.removeIf(relatedEntryBO -> relatedEntryBO.getId().equals(thinkTankDO.getId()) && relatedEntryBO.getCanonicalUrl().equals(thinkTankDO.getCanonicalUrl()));
            thinkTankDO1.setRelatedEntries(JSONUtil.toJsonStr((Object)relatedEntries));
        });
        this.updateBatchById((Collection)list);
        this.thinkTankMapper.deleteById((Serializable)((Object)id));
        return new WikiContentRemoveVO(id, removeList);
    }


    public void deleteThinkTankListByIds(List<String> ids) {
    }


    public WikiContentVO getThinkTank(String title, String id) {
        List list = ((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)this.lambdaQuery().like(StrUtil.isNotBlank((CharSequence)title), ThinkTankDO::getTitle, (Object)title)).eq(StrUtil.isNotBlank((CharSequence)id), ThinkTankDO::getId, (Object)id)).list();
        List userDOList = this.userService.lambdaQuery().select(new SFunction[]{UserDO::getId, UserDO::getUsername}).list();
        Map<Integer, String> userMap = userDOList.stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUsername));
        if (CollUtil.isEmpty((Collection)list)) {
            throw new ServiceException("词条不存在");
        }
        ThinkTankDO thinkTankDO = (ThinkTankDO)list.getFirst();
        List sections = ((LambdaQueryChainWrapper)this.sectionService.lambdaQuery().eq(SectionDO::getThinkTankId, (Object)thinkTankDO.getId())).list();
        List sectionBOS = SectionConvertUtil.convertToBO((List)sections);
        return this.getWikiContentVO(thinkTankDO, sectionBOS, userMap);
    }


    private WikiContentVO getWikiContentVO(ThinkTankDO thinkTankDO, List<SectionBO> sectionBOS, Map<Integer, String> userMap) {
        ContentBO contentBO = new ContentBO(thinkTankDO.getSummary(), sectionBOS);
        return WikiContentVO.builder().id(thinkTankDO.getId()).title(thinkTankDO.getTitle()).canonicalUrl(thinkTankDO.getCanonicalUrl()).creator(thinkTankDO.getCreator()).creatorName(StrUtil.isBlank((CharSequence)thinkTankDO.getCreator()) ? "" : userMap.get(Integer.valueOf(thinkTankDO.getCreator()))).createAt(thinkTankDO.getCreateAt()).updateAt(thinkTankDO.getUpdateAt()).view(thinkTankDO.getView()).state(thinkTankDO.getState()).stateMessage(thinkTankDO.getStateMessage()).totalWords(thinkTankDO.getTotalWords()).chaptersNum(JSONUtil.toList((String)thinkTankDO.getChaptersNum(), Long.class)).content(contentBO).totalCitationsNum(Integer.valueOf(JSONUtil.toList((String)thinkTankDO.getCitations(), CitationBO.class).size())).citations(JSONUtil.toList((String)thinkTankDO.getCitations(), CitationBO.class)).relatedEntries(JSONUtil.toList((String)thinkTankDO.getRelatedEntries(), RelatedEntryBO.class)).build();
    }


    public void addView(String id) {
        ((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)this.lambdaUpdate().setSql("view = view + 1", new Object[0])).eq(ThinkTankDO::getId, (Object)id)).update();
    }


    public PageResult<WikiListVO> getThinkTankList(String pageNum, String pageSize, String sortBy, String order, List<String> status, String creator, String q) {
        Page page = (Page)((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)((LambdaQueryChainWrapper)this.lambdaQuery().in(CollUtil.isNotEmpty(status), ThinkTankDO::getState, status)).eq(StrUtil.isNotBlank((CharSequence)creator), ThinkTankDO::getCreator, (Object)creator)).like(StrUtil.isNotBlank((CharSequence)q), ThinkTankDO::getTitle, (Object)q)).last("ORDER BY " + sortBy + " " + order)).page((IPage)Page.of((long)Long.parseLong(pageNum), (long)Long.parseLong(pageSize)));
        if (CollUtil.isEmpty((Collection)page.getRecords())) {
            return new PageResult(new ArrayList(), Long.valueOf(page.getTotal()), Long.valueOf(page.getCurrent()), Long.valueOf(page.getPages()));
        }
        List<String> userIds = page.getRecords().stream().map(ThinkTankDO::getCreator).toList();
        Map<Object, Object> map = new HashMap();
        if (CollUtil.isNotEmpty(userIds)) {
            List userDOS = ((LambdaQueryChainWrapper)this.userService.lambdaQuery().in(UserDO::getId, userIds)).list();
            map = userDOS.stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUsername));
        }
        List sections = ((LambdaQueryChainWrapper)this.sectionService.lambdaQuery().in(SectionDO::getThinkTankId, page.getRecords().stream().map(ThinkTankDO::getId).toList())).list();
        Map<String, List<SectionDO>> collect = sections.stream().collect(Collectors.groupingBy(SectionDO::getThinkTankId));
        Map<String, List> resultMap = collect.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> SectionConvertUtil.convertToBO((List)((List)entry.getValue()))));
        ArrayList wikiListVOS = new ArrayList();
        Map<Object, Object> finalMap = map;
        page.getRecords().forEach(sectionDO -> {
            WikiContentVO wikiContentVO = this.getWikiContentVO(sectionDO, resultMap.getOrDefault(sectionDO.getId(), Collections.emptyList()), finalMap);
            WikiListVO wikiListVO = new WikiListVO();
            BeanUtil.copyProperties((Object)wikiContentVO, (Object)wikiListVO, (String[])new String[0]);
            wikiListVO.setCitationNum(Integer.valueOf(wikiContentVO.getCitations().size()));
            wikiListVO.setRelatedEntriesNum(Integer.valueOf(wikiContentVO.getRelatedEntries().size()));
            wikiListVOS.add(wikiListVO);
        });
        return new PageResult(wikiListVOS, Long.valueOf(page.getTotal()), Long.valueOf(page.getCurrent()), Long.valueOf(page.getPages()));
    }


    public void approval(ThinkTankApprovalDTO dto) {
        ((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)((LambdaUpdateChainWrapper)this.lambdaUpdate().eq(ThinkTankDO::getId, (Object)dto.getId())).set(ThinkTankDO::getState, (Object)dto.getStatus())).set(ThinkTankDO::getStateMessage, (Object)dto.getMessage())).update();
    }
}
