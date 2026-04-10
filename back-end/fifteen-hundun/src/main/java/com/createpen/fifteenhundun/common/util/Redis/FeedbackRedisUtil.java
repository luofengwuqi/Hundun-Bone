package com.createpen.fifteenhundun.common.util.Redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.createpen.fifteenhundun.common.constant.RedisConstant.FEEDBACK_CACHE_PATH;

@Slf4j
@Component
public class FeedbackRedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public FeedbackRedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void clearFeedbacksCache() {
        log.info("清除反馈缓存...");
        redisTemplate.delete(FEEDBACK_CACHE_PATH);
    }
}
