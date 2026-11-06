package com.kaoyan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(KaoyanProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int timeout = properties.getLlm().getTimeoutSeconds() * 1000;
        factory.setConnectTimeout(Math.min(timeout, 15000));
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }
}
