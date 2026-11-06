package com.kaoyan.common.enums;

import lombok.Getter;

/**
 * 角色编码
 */
@Getter
public enum RoleCode {

    STUDENT("STUDENT", "考研学生"),
    ADMIN("ADMIN", "管理员");

    private final String code;
    private final String desc;

    RoleCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
