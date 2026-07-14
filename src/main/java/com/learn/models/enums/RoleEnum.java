package com.learn.models.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/*
 * Exception performing whole class analysis ignored.
 */
public enum RoleEnum {
    NORMAL("normal", "普通用户", Integer.valueOf(4)),
    DEVELOPER("developer", "开发者", Integer.valueOf(3)),
    ADMIN("admin", "管理员", Integer.valueOf(2)),
    SUPER_ADMIN("superAdmin", "超级管理员", Integer.valueOf(1));

    private final String name;
    private final String code;
    private final Integer id;

    public static List<Integer> getRoleIds(String role) {
        RoleEnum current = Arrays.stream(RoleEnum.values()).filter(r -> r.getName().equalsIgnoreCase(role)).findFirst().orElse(null);
        if (current == null) {
            return List.of();
        }
        return Arrays.stream(RoleEnum.values()).map(RoleEnum::getId).filter(rId -> rId >= current.getId()).sorted().collect(Collectors.toList());
    }


    public String getName() {
        return this.name;
    }


    public String getCode() {
        return this.code;
    }


    public Integer getId() {
        return this.id;
    }
    private RoleEnum(String name, String code, Integer id) {
        this.name = name;
        this.code = code;
        this.id = id;
    }
}
