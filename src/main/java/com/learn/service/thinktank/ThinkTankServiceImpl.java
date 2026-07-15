package com.learn.service.thinktank;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
        ThinkTankDO build = ThinkTankDO.builder()
            .id(dto.getId())
            .title(dto.getWikiEntryInfo().getTitle())
            .canonicalUrl("/page/" + dto.getWikiEntryInfo().getTitle())
            .summary(dto.getWikiContent().getSummary())
            .creator(userIdStr)
            .createAt(Instant.now().getEpochSecond())
            .updateAt(Instant.now().getEpochSecond())
            .view(0L)
            .totalWords(SectionConvertUtil.getTotalWords(dto.getWikiEntryInfo().getTitle(), dto.getWikiContent().getSections()))
            .chaptersNum(JSONUtil.toJsonStr(SectionConvertUtil.getChapterNumber(dto.getWikiContent().getSections())))
            .citations(JSONUtil.toJsonStr(dto.getCitations()))
            .build();
        if (CollUtil.isNotEmpty(dto.getRelatedEntries())) {
            build.setRelatedEntries(JSONUtil.toJsonStr(dto.getRelatedEntries()));
        }
        List<SectionDO> checkSectionDOS = new ArrayList<>();
        if (StrUtil.isEmpty(dto.getId()) || "0".equals(dto.getId())) {
            Long count = this.lambdaQuery()
                .select(ThinkTankDO::getCanonicalUrl)
                .eq(ThinkTankDO::getCanonicalUrl, build.getCanonicalUrl())
                .count();
            if (count > 0L) {
                throw new ServiceException("词条已存在");
            }
            build.setState(WikiStateEnum.PENDING_REVIEW.getCode());
            build.setStateMessage("");
            build.setId(null);
            this.thinkTankMapper.insert(build);
            List<SectionBO> sections = dto.getWikiContent().getSections();
            List<SectionDO> sectionDOS = SectionConvertUtil.convertToDO(build.getId(), sections, false);
            this.sectionService.saveBatch(sectionDOS);
            checkSectionDOS.addAll(sectionDOS);
        } else {
            ThinkTankDO thinkTankDO = this.thinkTankMapper.selectById(build.getId());
            if (thinkTankDO == null) {
                throw new ServiceException("词条不存在");
            }
            build.setView(thinkTankDO.getView() + 1L);
            build.setCreator(thinkTankDO.getCreator());
            this.thinkTankMapper.insertOrUpdate(build);
            List<SectionBO> sections = dto.getWikiContent().getSections();
            List<SectionDO> sectionDOS = SectionConvertUtil.convertToDO(build.getId(), sections, true);
            this.sectionService.saveOrUpdateBatch(sectionDOS);
            if (!thinkTankDO.getTitle().equals(dto.getWikiEntryInfo().getTitle())) {
                log.info("更新相关引用的词条标题内容");
                List<ThinkTankDO> list = this.lambdaQuery()
                    .like(ThinkTankDO::getRelatedEntries, thinkTankDO.getId())
                    .list();
                for (ThinkTankDO thinkTankDO1 : list) {
                    String relatedEntries = thinkTankDO1.getRelatedEntries();
                    List<RelatedEntryBO> relatedEntryBOs = JSONUtil.toList(relatedEntries, RelatedEntryBO.class);
                    relatedEntryBOs.removeIf(relatedEntryBO -> relatedEntryBO.getId().equals(thinkTankDO.getId()));
                    relatedEntryBOs.add(new RelatedEntryBO(thinkTankDO.getId(), build.getTitle(), build.getCanonicalUrl()));
                    thinkTankDO1.setRelatedEntries(JSONUtil.toJsonStr(relatedEntryBOs));
                    this.thinkTankMapper.updateById(thinkTankDO1);
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
            List<ThinkTankDO> list = this.lambdaQuery()
                .select(ThinkTankDO::getId, ThinkTankDO::getTitle, ThinkTankDO::getCanonicalUrl)
                .ne(StrUtil.isNotBlank(build.getId()), ThinkTankDO::getId, build.getId())
                .list();
            if (CollUtil.isNotEmpty(list) && CollUtil.isNotEmpty(checkSectionDOS)) {
                String allContent = checkSectionDOS.stream().map(SectionDO::getContent).collect(Collectors.joining());
                HashSet<RelatedEntryBO> set = new HashSet<>();
                for (ThinkTankDO t : list) {
                    if (!allContent.contains(t.getTitle())) continue;
                    set.add(new RelatedEntryBO(t.getId(), t.getTitle(), t.getCanonicalUrl()));
                }
                this.lambdaUpdate()
                    .set(ThinkTankDO::getRelatedEntries, JSONUtil.toJsonStr(set))
                    .eq(ThinkTankDO::getId, build.getId())
                    .update();
            }
        }).start();
    }

    @Transactional(rollbackFor={Exception.class})
    public String aiCreateThinkTank(WikiSaveDTO dto) {
        ThinkTankDO build = ThinkTankDO.builder()
            .id(dto.getId())
            .title(dto.getWikiEntryInfo().getTitle())
            .canonicalUrl("/page/" + dto.getWikiEntryInfo().getTitle())
            .summary(dto.getWikiContent().getSummary())
            .createAt(Instant.now().getEpochSecond())
            .updateAt(Instant.now().getEpochSecond())
            .view(0L)
            .totalWords(SectionConvertUtil.getTotalWords(dto.getWikiEntryInfo().getTitle(), dto.getWikiContent().getSections()))
            .chaptersNum(JSONUtil.toJsonStr(SectionConvertUtil.getChapterNumber(dto.getWikiContent().getSections())))
            .citations(JSONUtil.toJsonStr(dto.getCitations()))
            .build();
        ThinkTankDO thinkTankDO = this.thinkTankMapper.selectById(build.getId());
        if (thinkTankDO == null) {
            throw new ServiceException("词条不存在");
        }
        build.setView(thinkTankDO.getView() + 1L);
        this.thinkTankMapper.insertOrUpdate(build);
        List<SectionBO> sections = dto.getWikiContent().getSections();
        List<SectionDO> sectionDOS = SectionConvertUtil.convertToDO(build.getId(), sections, false);
        this.sectionService.saveOrUpdateBatch(sectionDOS);
        if (!thinkTankDO.getTitle().equals(dto.getWikiEntryInfo().getTitle())) {
            log.info("更新相关引用的词条标题内容");
            List<ThinkTankDO> list = this.lambdaQuery()
                .like(ThinkTankDO::getRelatedEntries, thinkTankDO.getId())
                .list();
            for (ThinkTankDO thinkTankDO1 : list) {
                String relatedEntries = thinkTankDO1.getRelatedEntries();
                List<RelatedEntryBO> relatedEntryBOs = JSONUtil.toList(relatedEntries, RelatedEntryBO.class);
                relatedEntryBOs.removeIf(relatedEntryBO -> relatedEntryBO.getId().equals(thinkTankDO.getId()));
                relatedEntryBOs.add(new RelatedEntryBO(thinkTankDO.getId(), build.getTitle(), build.getCanonicalUrl()));
                thinkTankDO1.setRelatedEntries(JSONUtil.toJsonStr(relatedEntryBOs));
                this.thinkTankMapper.updateById(thinkTankDO1);
            }
        } else {
            log.info("标题相同不需要更新相关内容");
        }
        this.addRelateEntries(build, sectionDOS);
        return build.getId();
    }

    @Transactional(rollbackFor={Exception.class})
    public WikiContentRemoveVO deleteThinkTank(String id) {
        ThinkTankDO thinkTankDO = (ThinkTankDO) this.getById(id);
        if (thinkTankDO == null) {
            throw new ServiceException("词条不存在");
        }
        List<ThinkTankDO> list = this.lambdaQuery()
            .select(ThinkTankDO::getRelatedEntries)
            .like(ThinkTankDO::getRelatedEntries, thinkTankDO.getId())
            .like(ThinkTankDO::getRelatedEntries, thinkTankDO.getCanonicalUrl())
            .list();
        if (!CollUtil.isNotEmpty(list)) {
            log.info("相关词条内容为空");
        }
        List<String> removeList = list.stream().map(ThinkTankDO::getId).toList();
        list.forEach(thinkTankDO1 -> {
            List<RelatedEntryBO> relatedEntries = JSONUtil.toList(thinkTankDO1.getRelatedEntries(), RelatedEntryBO.class);
            relatedEntries.removeIf(relatedEntryBO -> relatedEntryBO.getId().equals(thinkTankDO.getId()) && relatedEntryBO.getCanonicalUrl().equals(thinkTankDO.getCanonicalUrl()));
            thinkTankDO1.setRelatedEntries(JSONUtil.toJsonStr(relatedEntries));
        });
        this.updateBatchById(list);
        this.thinkTankMapper.deleteById(id);
        return new WikiContentRemoveVO(id, removeList);
    }

    public void deleteThinkTankListByIds(List<String> ids) {
    }

    public WikiContentVO getThinkTank(String title, String id) {
        List<ThinkTankDO> list = this.lambdaQuery()
            .like(StrUtil.isNotBlank(title), ThinkTankDO::getTitle, title)
            .eq(StrUtil.isNotBlank(id), ThinkTankDO::getId, id)
            .list();
        List<UserDO> userDOList = this.userService.lambdaQuery()
            .select(UserDO::getId, UserDO::getUsername)
            .list();
        Map<Integer, String> userMap = userDOList.stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUsername));
        if (CollUtil.isEmpty(list)) {
            throw new ServiceException("词条不存在");
        }
        ThinkTankDO thinkTankDO = list.getFirst();
        List<SectionDO> sections = this.sectionService.lambdaQuery()
            .eq(SectionDO::getThinkTankId, thinkTankDO.getId())
            .list();
        List<SectionBO> sectionBOS = SectionConvertUtil.convertToBO(sections);
        return this.getWikiContentVO(thinkTankDO, sectionBOS, userMap);
    }

    private WikiContentVO getWikiContentVO(ThinkTankDO thinkTankDO, List<SectionBO> sectionBOS, Map<Integer, String> userMap) {
        ContentBO contentBO = new ContentBO(thinkTankDO.getSummary(), sectionBOS);
        return WikiContentVO.builder()
            .id(thinkTankDO.getId())
            .title(thinkTankDO.getTitle())
            .canonicalUrl(thinkTankDO.getCanonicalUrl())
            .creator(thinkTankDO.getCreator())
            .creatorName(StrUtil.isBlank(thinkTankDO.getCreator()) ? "" : userMap.get(Integer.valueOf(thinkTankDO.getCreator())))
            .createAt(thinkTankDO.getCreateAt())
            .updateAt(thinkTankDO.getUpdateAt())
            .view(thinkTankDO.getView())
            .state(thinkTankDO.getState())
            .stateMessage(thinkTankDO.getStateMessage())
            .totalWords(thinkTankDO.getTotalWords())
            .chaptersNum(JSONUtil.toList(thinkTankDO.getChaptersNum(), Long.class))
            .content(contentBO)
            .totalCitationsNum(JSONUtil.toList(thinkTankDO.getCitations(), CitationBO.class).size())
            .citations(JSONUtil.toList(thinkTankDO.getCitations(), CitationBO.class))
            .relatedEntries(JSONUtil.toList(thinkTankDO.getRelatedEntries(), RelatedEntryBO.class))
            .build();
    }

    public void addView(String id) {
        this.lambdaUpdate().setSql("view = view + 1").eq(ThinkTankDO::getId, id).update();
    }

    public PageResult<WikiListVO> getThinkTankList(String pageNum, String pageSize, String sortBy, String order, List<String> status, String creator, String q) {
        Page<ThinkTankDO> page = this.lambdaQuery()
            .in(CollUtil.isNotEmpty(status), ThinkTankDO::getState, status)
            .eq(StrUtil.isNotBlank(creator), ThinkTankDO::getCreator, creator)
            .like(StrUtil.isNotBlank(q), ThinkTankDO::getTitle, q)
            .last("ORDER BY " + sortBy + " " + order)
            .page(Page.of(Long.parseLong(pageNum), Long.parseLong(pageSize)));
        if (CollUtil.isEmpty(page.getRecords())) {
            return new PageResult<>(new ArrayList<>(), page.getTotal(), page.getCurrent(), page.getPages());
        }
        List<String> userIds = page.getRecords().stream().map(ThinkTankDO::getCreator).toList();
        Map<Integer, String> map = Map.of();
        if (CollUtil.isNotEmpty(userIds)) {
            List<UserDO> userDOS = this.userService.lambdaQuery().in(UserDO::getId, userIds).list();
            map = userDOS.stream().collect(Collectors.toMap(UserDO::getId, UserDO::getUsername));
        }
        List<SectionDO> sections = this.sectionService.lambdaQuery()
            .in(SectionDO::getThinkTankId, page.getRecords().stream().map(ThinkTankDO::getId).toList())
            .list();
        Map<String, List<SectionDO>> collect = sections.stream().collect(Collectors.groupingBy(SectionDO::getThinkTankId));
        Map<String, List<SectionBO>> resultMap = collect.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> SectionConvertUtil.convertToBO(entry.getValue())));
        List<WikiListVO> wikiListVOS = new ArrayList<>();
        Map<Integer, String> finalMap = map;
        page.getRecords().forEach(thinkTankDO -> {
            WikiContentVO wikiContentVO = this.getWikiContentVO(thinkTankDO,
                resultMap.getOrDefault(thinkTankDO.getId(), Collections.emptyList()), finalMap);
            WikiListVO wikiListVO = new WikiListVO();
            BeanUtil.copyProperties(wikiContentVO, wikiListVO);
            wikiListVO.setCitationNum(wikiContentVO.getCitations().size());
            wikiListVO.setRelatedEntriesNum(wikiContentVO.getRelatedEntries().size());
            wikiListVOS.add(wikiListVO);
        });
        return new PageResult<>(wikiListVOS, page.getTotal(), page.getCurrent(), page.getPages());
    }

    public void approval(ThinkTankApprovalDTO dto) {
        this.lambdaUpdate()
            .eq(ThinkTankDO::getId, dto.getId())
            .set(ThinkTankDO::getState, dto.getStatus())
            .set(ThinkTankDO::getStateMessage, dto.getMessage())
            .update();
    }
}
