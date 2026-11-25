package com.kaoyan.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无访问权限"),
    NOT_FOUND(404, "资源不存在"),

    TOO_MANY_REQUESTS(429, "操作过于频繁，请稍后再试"),

    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_DISABLED(1003, "账号已被禁用"),
    USERNAME_EXISTS(1004, "用户名已存在"),

    QUESTION_NOT_EXIST(2001, "错题不存在"),
    DIAGNOSIS_FAILED(2002, "AI 诊断失败，请重试"),
    DIAGNOSIS_RUNNING(2003, "诊断正在进行中"),
    DIAGNOSIS_TASK_NOT_EXIST(2004, "诊断任务不存在"),
    DIAGNOSIS_TASK_STATE_INVALID(2005, "诊断任务状态不允许当前操作"),

    COURSE_NOT_EXIST(2101, "课程不存在"),

    LLM_CONFIG_MISSING(3001, "大模型未配置 API Key"),
    LLM_TIMEOUT(3002, "大模型响应超时"),
    LLM_PARSE_ERROR(3003, "大模型返回格式解析失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
