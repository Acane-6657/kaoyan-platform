package com.kaoyan.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic 声明。诊断消息走异步：保存错题 -> 发消息 -> 消费者执行诊断流水线。
 */
@Configuration
public class KafkaTopicConfig {

    public static final String DIAGNOSIS_TOPIC = "diagnosis-topic";

    @Bean
    public NewTopic diagnosisTopic() {
        return TopicBuilder.name(DIAGNOSIS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
