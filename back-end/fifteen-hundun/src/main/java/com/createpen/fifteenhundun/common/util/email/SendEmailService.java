package com.createpen.fifteenhundun.common.util.email;

import com.createpen.fifteenhundun.common.exception.MyException.UserNotFoundException;
import com.createpen.fifteenhundun.common.util.Redis.UserRedisUtil;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.createpen.fifteenhundun.common.constant.email.TEXT.*;

@Slf4j
@Component
public class SendEmailService {
    private final UserMapper userMapper;
    private final UserRedisUtil userRedisUtil;

    public SendEmailService(UserMapper userMapper, UserRedisUtil userRedisUtil) {
        this.userMapper = userMapper;
        this.userRedisUtil = userRedisUtil;
    }
    @Async("emailExecutor")
    public void sendFeedbackFinishedMsgToUser(Long userId) {
        String feedbackMakerEmail = getEmail(userId);
        EmailCodeUtil.sendEmailCode(feedbackMakerEmail,FEEDBACK_FINISHED_MSG_TO_USER );
    }
    @Async("emailExecutor")
    public void sendFeedbackBanedMsgToUser(Long userId) {
        String feedbackMakerEmail = getEmail(userId);
        EmailCodeUtil.sendEmailCode(feedbackMakerEmail,FEEDBACK_BANED_MSG_TO_USER );
    }
    @Async("emailExecutor")
    public void sendFeedbackAddedMsgToAdmin(Long userId) {
        String feedbackMakerEmail = getEmail(userId);
        EmailCodeUtil.sendEmailCode(feedbackMakerEmail,FEEDBACK_ADDED_MSG_TO_ADMIN );
    }

    private String getEmail(Long userId){
        //检查用户是否存在
        User feedbackMaker = userRedisUtil.getUserInRedis(userId);
        if (feedbackMaker == null){
            log.info("user:data:id缓存未命中,开始查询数据库");
            feedbackMaker = userMapper.selectUserById(userId);
        }
        if(feedbackMaker == null){
            throw new UserNotFoundException("用户不存在");
        }else{
            //存在 放入缓存
            userRedisUtil.saveUserInRedis(feedbackMaker);
        }
        return feedbackMaker.getEmail();
    }
}
