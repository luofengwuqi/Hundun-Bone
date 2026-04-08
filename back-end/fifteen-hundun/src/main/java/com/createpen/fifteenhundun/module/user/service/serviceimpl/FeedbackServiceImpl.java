package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.exception.MyException.InsufficientPermissionsException;
import com.createpen.fifteenhundun.module.user.mapper.FeedbackMapper;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.DTO.UpdateFeedbackStatusDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.Feedback;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.service.FeedbackService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.createpen.fifteenhundun.common.constant.RedisConstant.*;
import static com.createpen.fifteenhundun.common.constant.UserStatusConstant.STATUS_ADMIN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final RedisTemplate<String,Object> redisTemplate;
    private final FeedbackMapper feedbackMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    
    public FeedbackServiceImpl(UserMapper userMapper, FeedbackMapper feedbackMapper, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.userMapper = userMapper;
        this.feedbackMapper = feedbackMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Feedback> selectAllFeedback() {
       //1 判断权限是否允许
        isAllowed();
        //2 查询信息
        //2.1 先查redis
        try {
            Object cachedData = redisTemplate.opsForValue().get(FEEDBACK_CACHE_PATH);
            if(cachedData != null){
                String json = (String) cachedData;
                List<Feedback> redisList = objectMapper.readValue(json, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Feedback.class));
                log.info("命中缓存，从 Redis 获取反馈数据");
                return redisList;
            }
        } catch (JsonProcessingException e) {
            log.error("反序列化缓存数据失败", e);
            redisTemplate.delete(FEEDBACK_CACHE_PATH);
        }
        
        //2.2 再查数据库
        log.info("缓存未命中，查询数据库");
        List<Feedback> feedbacks = feedbackMapper.selectAllFeedback();
        
        //2.3 将查询结果存入redis
        try {
            String json = objectMapper.writeValueAsString(feedbacks);
            redisTemplate.opsForValue().set(FEEDBACK_CACHE_PATH, json, FEEDBACK_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            log.info("已将反馈数据存入 Redis 缓存，过期时间：{}分钟", FEEDBACK_CACHE_EXPIRE_TIME);
        } catch (JsonProcessingException e) {
            log.error("序列化缓存数据失败", e);
        }
        
        return feedbacks;
    }

    @Override
    public void updateFeedbackStatus(UpdateFeedbackStatusDTO dto) {
        // 1. 判断权限是否允许
        isAllowed();
        
        // 2. 更新反馈状态
        int rows = feedbackMapper.updateFeedbackStatus(dto.getId(), dto.getStatus(), LocalDateTime.now(),dto.getHandleUserId());
        
        if(rows == 0){
            throw new RuntimeException("反馈不存在或更新失败");
        }
        
        log.info("成功更新反馈状态，反馈ID：{}，新状态：{}", dto.getId(), dto.getStatus());
        
        // 3. 清除缓存，确保下次查询获取最新数据
        redisTemplate.delete(FEEDBACK_CACHE_PATH);
        log.info("已清除反馈缓存，确保数据一致性");
    }







    //辅助方法 判断权限是否允许
    private void isAllowed(){
        String currentUsername = BaseContext.getCurrentUsername();
        User user = userMapper.getUserByUserName(currentUsername);
        if(user == null){
            throw new InsufficientPermissionsException("管理员不存在...");
        }
        else if(user.getStatus() < STATUS_ADMIN){
            throw new InsufficientPermissionsException("权限不足...");
        }
    }
}
