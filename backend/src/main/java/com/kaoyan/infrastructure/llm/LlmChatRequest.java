package com.kaoyan.infrastructure.llm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LlmChatRequest {

    private String model;
    private List<LlmMessage> messages;
    private Double temperature;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private Boolean stream;
    @JsonProperty("response_format")
    private LlmMessage.ResponseFormat responseFormat;
}
