package com.createpen.fifteenhundun.common.util.Redis;

import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.createpen.fifteenhundun.common.constant.redis.RedisConstant.*;

@Slf4j
@Component
public class UserRedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public UserRedisUtil(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void clearUsersCache() {
        String pattern = USERS_CACHE_PATH + "*";
        log.info("清除用户查询缓存，pattern:{}", pattern);

        try {
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

    public void clearUserdata(Long userId) {
        String key = USER_DATA_PATH + userId;
        log.info("清除用户data缓存，key:{}", key);
        try {
            redisTemplate.delete(key);
            log.info("已删除用户查询缓存 key:{}", key);
        } catch (Exception e) {
            log.error("清除用户data失败", e);
        }
    }

    public void saveUserInRedis(User u) {
        try{
            redisTemplate.opsForValue().set(USER_DATA_PATH + u.getId(), u, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("保存用户数据到redis失败", e);
            throw new RedisException("保存用户数据到redis失败");
        }
    }

    public User getUserInRedis(Long userId) {
        try{
            Object obj = redisTemplate.opsForValue().get(USER_DATA_PATH + userId);
            if (obj == null) {
                return null;
            }
            if (obj instanceof User) {
                return (User) obj;
            }
            return objectMapper.convertValue(obj, User.class);
        }catch (Exception e){
            log.error("从redis获取用户数据失败", e);
            throw new RedisException("从redis获取用户数据失败");
        }
    }

}
