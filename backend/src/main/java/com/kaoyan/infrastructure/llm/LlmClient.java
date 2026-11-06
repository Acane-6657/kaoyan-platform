package com.kaoyan.infrastructure.llm;

import com.kaoyan.common.exception.BizException;
import com.kaoyan.common.result.ResultCode;
import com.kaoyan.config.KaoyanProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 大模型客户端封装（OpenAI 兼容协议 /chat/completions）。
 * <p>
 * 统一处理：鉴权头、超时、重试、结构化 JSON 输出、调用耗时日志。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LlmClient {

    private final RestTemplate restTemplate;
    private final KaoyanProperties properties;

    /** 普通对话 */
    public String chat(String systemPrompt, String userPrompt) {
        return doChat(systemPrompt, userPrompt, null, false);
    }

    /** 要求返回 JSON 对象（配合 Prompt 中的格式约束） */
    public String chatJson(String systemPrompt, String userPrompt, Double temperature) {
        return doChat(systemPrompt, userPrompt, temperature, true);
    }

    private String doChat(String systemPrompt, String userPrompt, Double temperature, boolean jsonMode) {
        KaoyanProperties.Llm cfg = properties.getLlm();
        if (!StringUtils.hasText(cfg.getApiKey())) {
            throw BizException.of(ResultCode.LLM_CONFIG_MISSING);
        }

        LlmChatRequest request = new LlmChatRequest();
        request.setModel(cfg.getModel());
        request.setMessages(StringUtils.hasText(systemPrompt)
                ? List.of(LlmMessage.system(systemPrompt), LlmMessage.user(userPrompt))
                : List.of(LlmMessage.user(userPrompt)));
        request.setTemperature(temperature);
        request.setStream(false);
        if (jsonMode) {
            request.setResponseFormat(LlmMessage.ResponseFormat.json());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(cfg.getApiKey());
        HttpEntity<LlmChatRequest> entity = new HttpEntity<>(request, headers);

        String url = cfg.getBaseUrl().replaceAll("/+$", "") + "/chat/completions";
        int maxRetry = Math.max(0, cfg.getMaxRetry());

        for (int attempt = 0; attempt <= maxRetry; attempt++) {
            long start = System.currentTimeMillis();
            try {
                LlmChatResponse response = restTemplate.postForObject(url, entity, LlmChatResponse.class);
                long cost = System.currentTimeMillis() - start;
                String content = response == null ? null : response.firstContent();
                if (!StringUtils.hasText(content)) {
                    log.warn("大模型返回空内容, attempt={}, cost={}ms", attempt, cost);
                    continue;
                }
                log.info("大模型调用成功 model={}, cost={}ms, tokens={}", cfg.getModel(), cost,
                        response.getUsage() == null ? "-" : response.getUsage().getTotalTokens());
                return content;
            } catch (Exception e) {
                long cost = System.currentTimeMillis() - start;
                log.warn("大模型调用失败 attempt={}, cost={}ms, err={}", attempt, cost, e.getMessage());
                if (attempt == maxRetry) {
                    throw new BizException(ResultCode.LLM_TIMEOUT, "大模型调用失败: " + e.getMessage());
                }
            }
        }
        throw BizException.of(ResultCode.LLM_TIMEOUT);
    }
}
