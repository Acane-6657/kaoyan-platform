package com.kaoyan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 错题来源
 */
@Getter
public enum QuestionSource {

    REAL_EXAM(0, "真题"),
    MOCK(1, "模拟题"),
    PRACTICE(2, "练习题");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    QuestionSource(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
