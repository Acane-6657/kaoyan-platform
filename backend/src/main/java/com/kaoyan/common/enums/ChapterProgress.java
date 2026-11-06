package com.kaoyan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 章节学习进度
 */
@Getter
public enum ChapterProgress {

    NOT_STARTED(0, "未开始"),
    LEARNING(1, "学习中"),
    FINISHED(2, "已学完");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    ChapterProgress(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
