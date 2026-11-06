package com.kaoyan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 题目类型
 */
@Getter
public enum QuestionType {

    CHOICE(0, "选择题"),
    JUDGE(1, "判断题"),
    SUBJECTIVE(2, "简答题");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    QuestionType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
