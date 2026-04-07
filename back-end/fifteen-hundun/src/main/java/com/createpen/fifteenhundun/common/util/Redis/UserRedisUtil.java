package com.createpen.fifteenhundun.common.util.Redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.createpen.fifteenhundun.common.constant.RedisConstant.TOKEN_PATH;
import static com.createpen.fifteenhundun.common.constant.RedisConstant.USERS_CACHE_PATH;

@Slf4j
@Component
public class UserRedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public UserRedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void clearUsersCache() {
        String pattern = USERS_CACHE_PATH + "*";
        log.info("清除用户查询缓存，pattern:{}", pattern);

        try {
            //获取所有匹配的 key
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("已删除 {} 个用户查询缓存 key", keys.size());
            }
        } catch (Exception e) {
            log.error("清除用户查询缓存失败", e);
        }
    }

    public void clearUserToken(Long userId) {
        String key = TOKEN_PATH + userId;
        log.info("清除用户token缓存，key:{}", key);

        try {
            redisTemplate.delete(key);
            log.info("已删除用户查询缓存 key:{}", key);
        } catch (Exception e) {
            log.error("清除用户token失败", e);
        }
    }
}
