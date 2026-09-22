package com.createpen.fifteenhundun.common.util.email;

import com.createpen.fifteenhundun.common.exception.MyException.UserNotFoundException;
import com.createpen.fifteenhundun.module.user.cache.UserRedisService;
import com.createpen.fifteenhundun.module.user.mapper.UserMapper;
import com.createpen.fifteenhundun.module.admin.pojo.DTO.GetUsersDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.admin.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.createpen.fifteenhundun.common.constant.CommonConstant.LARGE_NUMBER;
import static com.createpen.fifteenhundun.common.constant.status.UserStatusConstant.STATUS_ADMIN;
import static com.createpen.fifteenhundun.common.constant.status.UserStatusConstant.STATUS_SUPER_ADMIN;
import static com.createpen.fifteenhundun.common.constant.email.TEXT.*;

@Slf4j
@Component
public class SendEmailService {
    private final AdminService adminService;
    private final UserMapper userMapper;
    private final UserRedisService userRedisService;

    public SendEmailService(AdminService adminService, UserMapper userMapper, UserRedisService userRedisService) {
        this.adminService = adminService;
        this.userMapper = userMapper;
        this.userRedisService = userRedisService;
    }
    @Async("emailExecutor")
    public void sendFeedbackFinishedMsgToUser(Long userId) {
        String feedbackMakerEmail = getEmail(userId);
        SendMsgUtil.sendSingleEmail(feedbackMakerEmail,FEEDBACK_FINISHED_MSG_TO_USER );
        log.info("反馈完成通知发送完成");
    }
    @Async("emailExecutor")
    public void sendFeedbackBanedMsgToUser(Long userId) {
        String feedbackMakerEmail = getEmail(userId);
        SendMsgUtil.sendSingleEmail(feedbackMakerEmail,FEEDBACK_BANED_MSG_TO_USER );
        log.info("反馈否决通知发送完成");
    }
    @Async("emailExecutor")
    public void sendFeedbackAddedMsgToAdmin() {
        ArrayList<String> emailList = new ArrayList<>();
        try {
            GetUsersDTO dto1 = new GetUsersDTO(1,LARGE_NUMBER,null, STATUS_ADMIN , null, null, null);
            GetUsersDTO dto2 = new GetUsersDTO(1,LARGE_NUMBER,null, STATUS_SUPER_ADMIN, null, null, null);
            
            // 查询普通管理员，如果没有则返回空列表
            //todo:管理员可以选择是否接受反馈邮件 如果不接收 那么就从集合中移除(需要一个设置表)
            List<User> adminList;
            try {
                adminList = adminService.getUsers(dto1).getList();
            } catch (Exception e) {
                log.warn("未查询到普通管理员：{}", e.getMessage());
                adminList = null;
            }
            
            // 查询超级管理员，如果没有则返回空列表
            List<User> superAdminList;
            try {
                superAdminList = adminService.getUsers(dto2).getList();
            } catch (Exception e) {
                log.warn("未查询到超级管理员：{}", e.getMessage());
                superAdminList = null;
            }
            
            // 合并两个列表
            if (adminList != null) {
                for(User user : adminList){
                    // 防止邮箱为空
                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        emailList.add(user.getEmail());
                    }
                }
            }
            
            if (superAdminList != null) {
                for(User user : superAdminList){
                    // 防止邮箱为空
                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        emailList.add(user.getEmail());
                    }
                }
            }
            
            if (emailList.isEmpty()) {
                log.warn("没有找到任何管理员邮箱，无法发送反馈添加通知");
                return;
            }
            int successCount = SendMsgUtil.sendEmailToEmailList(emailList, FEEDBACK_ADDED_MSG_TO_ADMIN);
            log.info("反馈添加通知发送完成，目标管理员数量：{}，成功发送：{}", emailList.size(), successCount);
        } catch (Exception e) {
            log.error("发送反馈添加通知给管理员时发生异常", e);
        }
    }

    private String getEmail(Long userId){
        //检查用户是否存在
        User feedbackMaker = userRedisService.getUserInRedis(userId);
        if (feedbackMaker == null){
            log.info("user:data:id缓存未命中,开始查询数据库");
            feedbackMaker = userMapper.selectUserById(userId);
        }
        if(feedbackMaker == null){
            throw new UserNotFoundException("用户不存在");
        }else{
            //存在 放入缓存
            userRedisService.saveUserInRedis(feedbackMaker);
        }
        return feedbackMaker.getEmail();
    }
}
