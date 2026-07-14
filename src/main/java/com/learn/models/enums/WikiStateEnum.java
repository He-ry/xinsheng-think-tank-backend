package com.learn.models.enums;

import java.util.List;
/*
 * Exception performing whole class analysis ignored.
 */
public enum WikiStateEnum {
    ALL("all", "所有状态"),
    PENDING_REVIEW("pending_review", "待审核"),
    PUBLISHED("published", "已发布"),
    BUILDING("building", "生成中"),
    BUILDING_FAILED("building_failed", "生成失败"),
    DISMISSED("dismissed", "驳回"),
    COMPLETED("completed", "生成完成");

    private final String code;
    private final String desc;

    private WikiStateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WikiStateEnum fromCode(String code) {
        for (WikiStateEnum state : WikiStateEnum.values()) {
            if (!state.code.equals(code)) continue;
            return state;
        }
        throw new IllegalArgumentException("Unknown state: " + code);
    }

    public static List<String> getAllState() {
        return List.of(PENDING_REVIEW.getCode(), PUBLISHED.getCode(), BUILDING.getCode(), BUILDING_FAILED.getCode(), DISMISSED.getCode(), COMPLETED.getCode());
    }


    public String getCode() {
        return this.code;
    }


    public String getDesc() {
        return this.desc;
    }
}
