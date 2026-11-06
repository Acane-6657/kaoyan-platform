package com.kaoyan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 题目难度
 */
@Getter
public enum Difficulty {

    EASY(0, "基础"),
    MEDIUM(1, "中等"),
    HARD(2, "难题");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    Difficulty(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
