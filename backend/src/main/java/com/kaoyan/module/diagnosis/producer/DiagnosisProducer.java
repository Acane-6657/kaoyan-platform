package com.kaoyan.module.diagnosis.producer;

import com.kaoyan.config.KafkaTopicConfig;
import com.kaoyan.module.diagnosis.message.DiagnosisMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiagnosisProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 投递诊断任务。以任务 ID 作为分区键，保证同一任务的消息进入同一分区、顺序消费。
     */
    public ListenableFuture<SendResult<String, Object>> send(Long taskId, Long questionId,
                                                             Long userId, String traceId) {
        DiagnosisMessage message = DiagnosisMessage.of(taskId, questionId, userId, traceId);

        ListenableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(KafkaTopicConfig.DIAGNOSIS_TOPIC, String.valueOf(taskId), message);
        future
                .addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        log.info("诊断消息已投递 taskId={}, traceId={}, questionId={}, partition={}, offset={}",
                                taskId, traceId, questionId,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("诊断消息投递失败 taskId={}, traceId={}, questionId={}",
                                taskId, traceId, questionId, ex);
                    }
                });
        return future;
    }

    public String send(Long taskId, Long questionId, Long userId) {
        String traceId = newTraceId();
        send(taskId, questionId, userId, traceId);
        return traceId;
    }

    /**
     * 健康检查使用的无业务任务投递入口。
     */
    public String send(Long questionId, Long userId) {
        return send(0L, questionId, userId);
    }

    public static String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
