package com.kaoyan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 错题错误类型
 */
@Getter
public enum ErrorType {

    CONCEPT("CONCEPT", "概念不清"),
    CALCULATION("CALCULATION", "计算失误"),
    THINKING("THINKING", "思路偏差"),
    CARELESS("CARELESS", "审题失误"),
    FORGETTING("FORGETTING", "知识点遗忘"),
    OTHER("OTHER", "其他");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    ErrorType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ErrorType of(String code) {
        if (code == null) {
            return null;
        }
        for (ErrorType t : values()) {
            if (t.code.equalsIgnoreCase(code)) {
                return t;
            }
        }
        return OTHER;
    }
}
