package com.kaoyan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 错题掌握状态
 */
@Getter
public enum MasterStatus {

    UNMASTERED(0, "未掌握"),
    REVIEWING(1, "待复习"),
    MASTERED(2, "已掌握");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    MasterStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
