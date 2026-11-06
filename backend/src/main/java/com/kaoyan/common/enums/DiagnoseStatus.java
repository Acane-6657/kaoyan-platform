package com.kaoyan.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 错题诊断状态（支撑异步诊断与前端轮询/SSE）
 */
@Getter
public enum DiagnoseStatus {

    PENDING(0, "待诊断"),
    RUNNING(1, "诊断中"),
    FINISHED(2, "已完成"),
    FAILED(3, "诊断失败");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    DiagnoseStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
