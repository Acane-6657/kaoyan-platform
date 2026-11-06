package com.kaoyan.module.diagnosis.producer;

import com.kaoyan.config.KafkaTopicConfig;
import com.kaoyan.module.diagnosis.message.DiagnosisMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiagnosisProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 投递诊断任务。以错题 ID 作为分区键，保证同一错题的消息进入同一分区、顺序消费。
     */
    public String send(Long questionId, Long userId) {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        DiagnosisMessage message = DiagnosisMessage.of(questionId, userId, traceId);

        kafkaTemplate.send(KafkaTopicConfig.DIAGNOSIS_TOPIC, String.valueOf(questionId), message)
                .addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        log.info("诊断消息已投递 traceId={}, questionId={}, partition={}, offset={}",
                                traceId, questionId,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("诊断消息投递失败 traceId={}, questionId={}", traceId, questionId, ex);
                    }
                });
        return traceId;
    }
}
