package com.learn.utils;

import cn.hutool.core.collection.CollUtil;
import com.learn.models.bo.SectionBO;
import com.learn.models.entity.SectionDO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
/*
 * Exception performing whole class analysis ignored.
 */
@Data
public class SectionConvertUtil {
    private static final Logger log = LoggerFactory.getLogger(SectionConvertUtil.class);

    public static List<SectionDO> convertToDO(String thinkTankId, List<SectionBO> sections, Boolean isUpdate) {
        ArrayList<SectionDO> result = new ArrayList<SectionDO>();
        if (sections == null || sections.isEmpty()) {
            log.info("sections为空");
            return result;
        }


        for (SectionBO section : sections) {
            SectionConvertUtil.traverse((SectionBO)section, (String)thinkTankId, null, result, (Boolean)isUpdate);
        }
        return result;
    }

    public static Long getTotalWords(String title, List<SectionBO> sections) {
        return (long)title.length() + SectionConvertUtil.countWords(sections);
    }

    public static List<Long> getChapterNumber(List<SectionBO> sections) {
        List sectionDOS = SectionConvertUtil.convertToDO((String)"", sections, (Boolean)false);
        if (CollUtil.isEmpty((Collection)sectionDOS)) {
            return Collections.emptyList();
        }
        List<Long> chaptersNum = sectionDOS.stream().collect(Collectors.groupingBy(SectionDO::getLevel, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList();
        return chaptersNum;
    }

    private static void traverse(SectionBO bo, String thinkTankId, String parentId, List<SectionDO> result, Boolean isUpdate) {
        String currentId = Boolean.TRUE.equals(isUpdate) ? bo.getId() : UUID.randomUUID().toString();
        SectionDO sectionDO = new SectionDO();
        sectionDO.setId(currentId);
        sectionDO.setThinkTankId(thinkTankId);
        sectionDO.setParentId(parentId);
        sectionDO.setHeading(bo.getHeading());
        sectionDO.setContent(bo.getContent());
        sectionDO.setLevel(bo.getLevel());
        result.add(sectionDO);
        if (bo.getChildren() == null || bo.getChildren().isEmpty()) {
            return;
        }
        for (SectionBO child : bo.getChildren()) {
            SectionConvertUtil.traverse((SectionBO)child, (String)thinkTankId, (String)currentId, result, (Boolean)isUpdate);
        }
    }

    public static List<SectionBO> convertToBO(List<SectionDO> sectionDOList) {
        if (sectionDOList == null || sectionDOList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, SectionBO> boMap = sectionDOList.stream().collect(Collectors.toMap(SectionDO::getId, SectionConvertUtil::toBO));
        ArrayList<SectionBO> roots = new ArrayList<SectionBO>();
        for (SectionDO sectionDO : sectionDOList) {
            SectionBO current = boMap.get(sectionDO.getId());
            String parentId = sectionDO.getParentId();
            if (parentId == null || parentId.isBlank()) {
                roots.add(current);
                continue;
            }
            SectionBO parent = boMap.get(parentId);
            if (parent == null) continue;
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList());
            }
            parent.getChildren().add(current);
        }
        return roots;
    }

    private static SectionBO toBO(SectionDO sectionDO) {
        SectionBO bo = new SectionBO();
        bo.setId(sectionDO.getId());
        bo.setHeading(sectionDO.getHeading());
        bo.setContent(sectionDO.getContent());
        bo.setLevel(sectionDO.getLevel());
        bo.setChildren(new ArrayList());
        return bo;
    }

    public static long countWords(List<SectionBO> sections) {
        if (sections == null || sections.isEmpty()) {
            return 0L;
        }
        long total = 0L;
        for (SectionBO section : sections) {
            total += SectionConvertUtil.countSection((SectionBO)section);
        }
        return total;
    }

    private static long countSection(SectionBO section) {
        long count = 0L;
        if (section.getHeading() != null) {
            count += (long)section.getHeading().length();
        }
        if (section.getContent() != null) {
            count += (long)section.getContent().length();
        }
        if (section.getChildren() != null && !section.getChildren().isEmpty()) {
            for (SectionBO child : section.getChildren()) {
                count += SectionConvertUtil.countSection((SectionBO)child);
            }
        }
        return count;
    }
}
