-- 信胜智库 数据库初始化脚本
-- 数据库需已存在: CREATE DATABASE IF NOT EXISTS `xinsheng-think-tank` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 百科词条主表
CREATE TABLE IF NOT EXISTS `t_think_tank` (
    `id` VARCHAR(64) NOT NULL COMMENT '词条唯一ID，UUID',
    `title` VARCHAR(255) NOT NULL COMMENT '词条标题',
    `canonical_url` VARCHAR(512) DEFAULT NULL COMMENT '规范化URL路径',
    `summary` TEXT COMMENT '词条摘要',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_at` BIGINT DEFAULT NULL COMMENT '创建时间，Unix时间戳（秒）',
    `update_at` BIGINT DEFAULT NULL COMMENT '修改时间，Unix时间戳（秒）',
    `view` BIGINT DEFAULT 0 COMMENT '查看数',
    `state` VARCHAR(32) DEFAULT NULL COMMENT '状态',
    `state_message` VARCHAR(512) DEFAULT NULL COMMENT '状态信息',
    `total_words` BIGINT DEFAULT 0 COMMENT '总字数统计',
    `chapters_num` VARCHAR(255) DEFAULT NULL COMMENT '章节数量统计数组',
    `citations` TEXT COMMENT '引用来源列表(JSON)',
    `related_entries` TEXT COMMENT '相关词条列表(JSON)',
    `files` TEXT COMMENT 'AI生成词条文件(JSON)',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_title` (`title`),
    KEY `idx_state` (`state`),
    KEY `idx_creator` (`creator`),
    KEY `idx_update_at` (`update_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='百科词条主表';

-- 词条章节表
CREATE TABLE IF NOT EXISTS `t_section` (
    `id` VARCHAR(64) NOT NULL COMMENT '章节唯一标识符',
    `think_tank_id` VARCHAR(64) NOT NULL COMMENT '所属词条ID',
    `parent_id` VARCHAR(64) DEFAULT NULL COMMENT '父章节ID，一级章节为NULL',
    `heading` VARCHAR(512) DEFAULT NULL COMMENT '章节标题',
    `content` MEDIUMTEXT COMMENT '章节内容（Markdown）',
    `level` INT DEFAULT NULL COMMENT '章节层级',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_think_tank_id` (`think_tank_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='词条章节表';

-- 评论表
CREATE TABLE IF NOT EXISTS `t_comment` (
    `id` VARCHAR(64) NOT NULL COMMENT '评论唯一ID',
    `parent_id` VARCHAR(64) DEFAULT NULL COMMENT '父评论ID',
    `root_id` VARCHAR(64) DEFAULT NULL COMMENT '根评论ID',
    `entry_id` VARCHAR(64) DEFAULT NULL COMMENT '词条ID',
    `content` TEXT COMMENT '评论内容',
    `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    `user_name` VARCHAR(255) DEFAULT NULL COMMENT '用户名',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `dislike_count` INT DEFAULT 0 COMMENT '点踩数',
    `reply_count` INT DEFAULT 0 COMMENT '子评论数',
    `created_at` BIGINT DEFAULT NULL COMMENT '创建时间，Unix时间戳',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_entry_id` (`entry_id`),
    KEY `idx_root_id` (`root_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评论表';

-- 系统信息表
CREATE TABLE IF NOT EXISTS `t_system` (
    `id` VARCHAR(64) NOT NULL COMMENT 'ID',
    `sys_key` VARCHAR(255) DEFAULT NULL COMMENT '系统键',
    `value` TEXT COMMENT '值',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_key` (`sys_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统信息表';

-- MySQL FULLTEXT indexes for Chinese full-text search (requires ngram parser)
-- Uses a stored procedure to create indexes safely — skips if already exists

DELIMITER //

CREATE PROCEDURE IF NOT EXISTS create_fulltext_index_if_not_exists(
    IN tbl_name VARCHAR(128),
    IN idx_name VARCHAR(128),
    IN idx_columns VARCHAR(512)
)
BEGIN
    DECLARE idx_count INT DEFAULT 0;
    SELECT COUNT(*) INTO idx_count
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tbl_name
      AND INDEX_NAME = idx_name;

    IF idx_count = 0 THEN
        SET @ddl = CONCAT('ALTER TABLE ', tbl_name, ' ADD FULLTEXT INDEX ', idx_name, ' (', idx_columns, ') WITH PARSER ngram');
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

DELIMITER ;

CALL create_fulltext_index_if_not_exists('t_think_tank', 'ft_think_tank_search', 'title, summary');
CALL create_fulltext_index_if_not_exists('t_section', 'ft_section_content', 'content');

DROP PROCEDURE IF EXISTS create_fulltext_index_if_not_exists;
