package com.learn.models.enums;

public enum SystemKeyEnum {
    TODAY_VIEWCOUNT("today_viewcount", "今日浏览量"),
    TOTAL_VIEWCOUNT("total_viewcount", "总浏览量");

    private final String key;
    private final String desc;
    public String getKey() {
        return this.key;
    }


    public String getDesc() {
        return this.desc;
    }
    private SystemKeyEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
