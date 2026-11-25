package com.kaoyan.module.diagnosis.consumer;

import com.kaoyan.config.KafkaTopicConfig;
import com.kaoyan.module.diagnosis.message.DiagnosisMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 诊断任务消费者。
 * <p>
 * 阶段二仅验证 Kafka 链路连通；阶段四在此接入诊断流水线
 * （考点映射 -> 错误类型 -> 错因 -> 解题思路 -> 举一反三），并通过 SseManager 分阶段推送。
 */
@Slf4j
@Component
public class DiagnosisConsumer {

    @KafkaListener(topics = KafkaTopicConfig.DIAGNOSIS_TOPIC, groupId = "kaoyan-platform")
    public void onMessage(DiagnosisMessage message) {
        log.info("收到诊断消息 taskId={}, traceId={}, questionId={}, userId={}, promptVersion={}",
                message.getTaskId(), message.getTraceId(), message.getQuestionId(),
                message.getUserId(), message.getPromptVersion());

        // TODO 阶段四：执行诊断流水线 + SSE 推送 + 结果入库
        // 幂等提示：消费前先按 taskId 校验任务状态，已成功或已取消时直接跳过
    }
}
