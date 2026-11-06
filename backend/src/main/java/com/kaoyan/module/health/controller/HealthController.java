package com.kaoyan.module.health.controller;

import com.kaoyan.common.result.Result;
import com.kaoyan.config.KaoyanProperties;
import com.kaoyan.module.diagnosis.producer.DiagnosisProducer;
import com.kaoyan.module.system.mapper.SysUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查与中间件连通性自检（阶段二验收用，无需登录）
 */
@Slf4j
@Api(tags = "00-健康检查")
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final SysUserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DiagnosisProducer diagnosisProducer;
    private final KaoyanProperties properties;

    @ApiOperation("存活探针")
    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong");
    }

    @ApiOperation("一次性检查所有依赖")
    @GetMapping("/all")
    public Result<Map<String, Object>> all() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("app", "UP");
        report.put("mysql", checkMysql());
        report.put("redis", checkRedis());
        report.put("llm", checkLlm());
        return Result.success(report);
    }

    @ApiOperation("MySQL 连通性")
    @GetMapping("/mysql")
    public Result<Object> mysql() {
        return Result.success(checkMysql());
    }

    @ApiOperation("Redis 读写")
    @GetMapping("/redis")
    public Result<Object> redis() {
        return Result.success(checkRedis());
    }

    @ApiOperation("Kafka 收发（投递一条测试诊断消息，观察控制台消费者日志）")
    @PostMapping("/kafka")
    public Result<Map<String, Object>> kafka(@RequestParam(defaultValue = "999999") Long questionId) {
        Map<String, Object> data = new LinkedHashMap<>();
        try {
            String traceId = diagnosisProducer.send(questionId, 0L);
            data.put("status", "SENT");
            data.put("traceId", traceId);
            data.put("topic", "diagnosis-topic");
            data.put("tip", "查看应用日志中 DiagnosisConsumer 的输出确认消费成功");
        } catch (Exception e) {
            log.error("Kafka 自检失败", e);
            data.put("status", "DOWN");
            data.put("error", e.getMessage());
        }
        return Result.success(data);
    }

    private Map<String, Object> checkMysql() {
        Map<String, Object> m = new LinkedHashMap<>();
        try {
            Long count = userMapper.selectCount(null);
            m.put("status", "UP");
            m.put("sysUserCount", count);
        } catch (Exception e) {
            m.put("status", "DOWN");
            m.put("error", rootMessage(e));
        }
        return m;
    }

    private Map<String, Object> checkRedis() {
        Map<String, Object> m = new LinkedHashMap<>();
        String key = "kaoyan:health:check";
        try {
            redisTemplate.opsForValue().set(key, "ok", Duration.ofSeconds(30));
            Object v = redisTemplate.opsForValue().get(key);
            m.put("status", "UP");
            m.put("readBack", v);
        } catch (Exception e) {
            m.put("status", "DOWN");
            m.put("error", rootMessage(e));
        }
        return m;
    }

    private Map<String, Object> checkLlm() {
        Map<String, Object> m = new LinkedHashMap<>();
        KaoyanProperties.Llm llm = properties.getLlm();
        m.put("configured", StringUtils.hasText(llm.getApiKey()));
        m.put("baseUrl", llm.getBaseUrl());
        m.put("model", llm.getModel());
        m.put("status", StringUtils.hasText(llm.getApiKey()) ? "READY" : "NO_API_KEY");
        return m;
    }

    private String rootMessage(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        String msg = t.getMessage();
        return msg == null ? t.getClass().getSimpleName() : msg;
    }
}
