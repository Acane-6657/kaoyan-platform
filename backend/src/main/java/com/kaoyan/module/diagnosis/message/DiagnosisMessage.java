package com.kaoyan.module.diagnosis.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 诊断异步消息体：保存错题后投递到 Kafka，由消费者执行诊断流水线。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisMessage implements Serializable {

    /** 诊断任务 ID */
    private Long taskId;

    /** 错题 ID */
    private Long questionId;

    /** 触发用户 ID */
    private Long userId;

    /** 链路追踪 ID，贯穿日志 */
    private String traceId;

    /** 提示词版本 */
    private String promptVersion;

    /** 投递时间戳 */
    private Long timestamp;

    public static DiagnosisMessage of(Long taskId, Long questionId, Long userId, String traceId) {
        return new DiagnosisMessage(
                taskId,
                questionId,
                userId,
                traceId,
                "diagnosis-v1",
                System.currentTimeMillis()
        );
    }
}
