package com.learn.models;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.learn.domain.vo.maxkb.MaxKbWikiContentVO;
import com.learn.models.bo.ContentBO;
import com.learn.models.bo.SectionBO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
@Data
public class StreamJsonParser {
    private static final Logger log = LoggerFactory.getLogger(StreamJsonParser.class);
    private final Map<String, StringBuilder> rawMap = new ConcurrentHashMap();
    private final Map<String, Integer> sectionIndexMap = new ConcurrentHashMap();

    public void append(String id, String token, MaxKbWikiContentVO maxKbWikiContentVO) {
        StringBuilder buf = this.rawMap.computeIfAbsent(id, k -> new StringBuilder());
        buf.append(token);
        String text = buf.toString();
        ContentBO content = maxKbWikiContentVO.getContent();
        this.parseSummaryAdvanced(text, content);
        this.parseSectionsIncremental(id, text, content);
    }


    private void parseSummaryAdvanced(String text, ContentBO content) {
        int key = text.indexOf("\"summary\"");
        if (key < 0) {
            return;
        }
        int first = text.indexOf("\"", key + 9);
        if (first < 0) {
            return;
        }
        int end = text.indexOf("\"", first + 1);
        String summary = end < 0 ? text.substring(first + 1) : text.substring(first + 1, end);
        content.setSummary(summary);
    }


    private void parseSectionsIncremental(String id, String text, ContentBO content) {
        int idIndex;
        List<SectionBO> sections = content.getSections();
        if (sections == null) {
            sections = new ArrayList<SectionBO>();
            content.setSections(sections);
        }
        int lastIndex = this.sectionIndexMap.getOrDefault(id, 0);
        while ((idIndex = text.indexOf("\"id\"", lastIndex)) >= 0) {
            int childrenArrayEnd;
            int childrenIndex;
            int sectionStart = text.lastIndexOf("{", idIndex);
            if (sectionStart < 0) {
                sectionStart = idIndex;
            }
            if ((childrenIndex = text.indexOf("\"children\"", idIndex)) < 0) break;
            int childrenArrayStart = text.indexOf("[", childrenIndex);
            if (text.startsWith("[]", childrenArrayStart)) {
                childrenArrayEnd = text.indexOf("}", childrenArrayStart);
            } else {
                int pos;
                int bracketCount = 1;
                for (pos = childrenArrayStart + 1; pos < text.length() && bracketCount > 0; ++pos) {
                    char c = text.charAt(pos);
                    if (c == '[') {
                        ++bracketCount;
                        continue;
                    }
                    if (c != ']') continue;
                    --bracketCount;
                }
                childrenArrayEnd = pos;
                childrenArrayEnd = text.indexOf("}", childrenArrayEnd);
            }
            if (childrenArrayEnd < 0) break;
            String sectionStr = text.substring(sectionStart, childrenArrayEnd + 1);
            JSONObject sectionObj = JSONUtil.parseObj((String)sectionStr);
            sectionObj.set("id", (Object)UUID.randomUUID().toString());
            SectionBO sectionBO = (SectionBO)JSONUtil.toBean((JSONObject)sectionObj, SectionBO.class);
            this.replaceIds(sectionBO);
            boolean exists = false;
            for (int i = 0; i < sections.size(); ++i) {
                if (!((SectionBO)sections.get(i)).getId().equals(sectionObj.getStr("id"))) continue;
                exists = true;
                sections.set(i, sectionBO);
                break;
            }


            if (!exists) {
                sections.add(sectionBO);
            }
            lastIndex = childrenArrayEnd + 1;
            this.sectionIndexMap.put(id, lastIndex);
        }
    }

    private void replaceIds(SectionBO node) {
        node.setId(UUID.randomUUID().toString());
        if (node.getChildren() == null) {
            return;
        }
        for (SectionBO child : node.getChildren()) {
            this.replaceIds(child);
        }
    }

    public void clear(String id) {
        this.rawMap.remove(id);
        this.sectionIndexMap.remove(id);
    }
}
