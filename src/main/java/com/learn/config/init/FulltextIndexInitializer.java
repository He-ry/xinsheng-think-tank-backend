package com.learn.config.init;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FulltextIndexInitializer {

    private static final Logger log = LoggerFactory.getLogger(FulltextIndexInitializer.class);

    private static final String INDEX_THINK_TANK = "ft_think_tank_search";
    private static final String INDEX_SECTION = "ft_section_content";

    private static final String CREATE_THINK_TANK_IDX =
        "ALTER TABLE t_think_tank ADD FULLTEXT INDEX ft_think_tank_search (title, summary) WITH PARSER ngram";
    private static final String CREATE_SECTION_IDX =
        "ALTER TABLE t_section ADD FULLTEXT INDEX ft_section_content (content) WITH PARSER ngram";

    private final JdbcTemplate jdbcTemplate;

    public FulltextIndexInitializer(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureFulltextIndexes() {
        ensureIndex("t_think_tank", INDEX_THINK_TANK, CREATE_THINK_TANK_IDX);
        ensureIndex("t_section", INDEX_SECTION, CREATE_SECTION_IDX);
    }

    private void ensureIndex(String table, String indexName, String createDdl) {
        String sql = "SELECT COUNT(1) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND INDEX_NAME = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, table, indexName);
        if (count != null && count > 0) {
            log.info("FULLTEXT index {} already exists on {}, skipped", indexName, table);
            return;
        }
        try {
            jdbcTemplate.execute(createDdl);
            log.info("FULLTEXT index {} created on {}", indexName, table);
        } catch (Exception e) {
            log.error("Failed to create FULLTEXT index {} on {}: {}", indexName, table, e.getMessage());
        }
    }
}
