package com.learn.models.mapper;

import com.learn.config.mybatis.BaseMapperX;
import com.learn.models.entity.ThinkTankDO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ThinkTankMapper
extends BaseMapperX<ThinkTankDO> {

    @Select("""
        SELECT t.id, t.title, t.canonical_url, t.summary, t.creator,
               t.create_at, t.update_at, t.view, t.state, t.state_message,
               t.total_words, t.chapters_num, t.citations, t.related_entries, t.files,
               GREATEST(
                 COALESCE(MATCH(t.title, t.summary) AGAINST(#{keyword} IN BOOLEAN MODE), 0),
                 COALESCE(MAX(MATCH(s.content) AGAINST(#{keyword} IN BOOLEAN MODE)), 0)
               ) AS relevance
        FROM t_think_tank t
        LEFT JOIN t_section s ON s.think_tank_id = t.id AND s.deleted = 0
        WHERE t.state = 'published' AND t.deleted = 0
          AND (
            MATCH(t.title, t.summary) AGAINST(#{keyword} IN BOOLEAN MODE)
            OR MATCH(s.content) AGAINST(#{keyword} IN BOOLEAN MODE)
          )
        GROUP BY t.id
        ORDER BY relevance DESC
        LIMIT #{offset}, #{limit}
        """)
    List<ThinkTankDO> fulltextSearch(@Param("keyword") String keyword,
                                     @Param("offset") long offset,
                                     @Param("limit") int limit);

    @Select("""
        SELECT COUNT(*)
        FROM t_think_tank t
        WHERE t.state = 'published' AND t.deleted = 0
          AND (
            MATCH(t.title, t.summary) AGAINST(#{keyword} IN BOOLEAN MODE)
            OR EXISTS (
              SELECT 1 FROM t_section s
              WHERE s.think_tank_id = t.id AND s.deleted = 0
                AND MATCH(s.content) AGAINST(#{keyword} IN BOOLEAN MODE)
            )
          )
        """)
    long fulltextSearchCount(@Param("keyword") String keyword);
}
