package com.kaoyan.infrastructure.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE 连接管理器：按业务 key（如错题 ID）维护连接，支持分阶段推送诊断进度。
 */
@Slf4j
@Component
public class SseManager {

    /** 默认超时 5 分钟 */
    private static final long DEFAULT_TIMEOUT = 5 * 60 * 1000L;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter create(String key) {
        return create(key, DEFAULT_TIMEOUT);
    }

    public SseEmitter create(String key, long timeoutMillis) {
        // 同一 key 重复订阅时，先关闭旧连接
        complete(key);

        SseEmitter emitter = new SseEmitter(timeoutMillis);
        emitter.onCompletion(() -> {
            emitters.remove(key);
            log.debug("SSE 完成: {}", key);
        });
        emitter.onTimeout(() -> {
            emitters.remove(key);
            emitter.complete();
            log.debug("SSE 超时: {}", key);
        });
        emitter.onError(e -> {
            emitters.remove(key);
            log.debug("SSE 异常: {} -> {}", key, e.getMessage());
        });
        emitters.put(key, emitter);
        return emitter;
    }

    /** 推送一个命名事件；连接不存在或已断开时静默忽略 */
    public void send(String key, String eventName, Object data) {
        SseEmitter emitter = emitters.get(key);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (IOException | IllegalStateException e) {
            log.debug("SSE 推送失败 {}: {}", key, e.getMessage());
            emitters.remove(key);
        }
    }

    public void complete(String key) {
        SseEmitter emitter = emitters.remove(key);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception ignored) {
                // 已完成或客户端断开，忽略
            }
        }
    }

    public boolean isActive(String key) {
        return emitters.containsKey(key);
    }

    public int activeCount() {
        return emitters.size();
    }
}
