package com.kaoyan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kaoyan")
public class KaoyanProperties {

    private Jwt jwt = new Jwt();
    private Llm llm = new Llm();
    private Kafka kafka = new Kafka();
    private Upload upload = new Upload();

    @Data
    public static class Jwt {
        private String secret;
        private long expireMinutes = 720;
        private String header = "Authorization";
        private String prefix = "Bearer ";
    }

    @Data
    public static class Llm {
        private String baseUrl = "https://api.deepseek.com";
        private String apiKey;
        private String model = "deepseek-chat";
        private int timeoutSeconds = 60;
        private int maxRetry = 1;
    }

    @Data
    public static class Kafka {
        private String diagnosisTopic = "diagnosis-topic";
    }

    @Data
    public static class Upload {
        private String dir = "./uploads";
    }
}
