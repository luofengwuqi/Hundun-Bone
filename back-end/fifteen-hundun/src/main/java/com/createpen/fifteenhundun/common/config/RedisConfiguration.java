package com.createpen.fifteenhundun.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfiguration {
    public RedisConfiguration() {
        log.info("RedisConfiguration init");
    }

    @Bean
    public RedisTemplate getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建 redis 模版对象.............");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置 key 的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置 value 的序列化器（解决存储字符串时的乱码问题）
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        //设置自动过期时间


        log.info("创建 redis 模版对象成功.............");
        return redisTemplate;
    }
}
