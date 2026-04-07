package com.createpen.fifteenhundun.module.user.service.serviceimpl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.exception.MyException.InsufficientPermissionsException;
import com.createpen.fifteenhundun.module.user.mapper.FeedbackMapper;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.user.pojo.Entity.Feedback;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.createpen.fifteenhundun.common.constant.UserStatusConstant.STATUS_ADMIN;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;
    private final UserMapper userMapper;
    public FeedbackServiceImpl(UserMapper userMapper, FeedbackMapper feedbackMapper) {
        this.userMapper = userMapper;
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public List<Feedback> selectAllFeedback() {
       //1 判断权限是否允许
        isAllowed();
        //2 查询信息
        return feedbackMapper.selectAllFeedback();
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
