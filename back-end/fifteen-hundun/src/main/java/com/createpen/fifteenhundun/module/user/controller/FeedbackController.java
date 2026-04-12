package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.user.pojo.DTO.AddFeedbackDTO;
import com.createpen.fifteenhundun.module.user.pojo.DTO.UpdateFeedbackStatusDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.Feedback;
import com.createpen.fifteenhundun.module.user.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/feedback")
@Validated
public class FeedbackController {
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }


    //获取所有的反馈
    @GetMapping
    public Result<List<Feedback>> selectAllFeedback(){
        log.info("获取所有的反馈");
        return Result.success(feedbackService.selectAllFeedback());
    }
    
    //更新反馈状态
    @PutMapping("/status")
    public Result<Void> updateFeedbackStatus(@Valid @RequestBody UpdateFeedbackStatusDTO dto){
        log.info("更新反馈状态，反馈ID：{}，新状态：{},处理人:{}", dto.getId(), dto.getStatus(),dto.getHandleUserId());
        feedbackService.updateFeedbackStatus(dto);
        return Result.success();
    }

    @PostMapping
    public Result<Void> addFeedback(@Valid @RequestBody AddFeedbackDTO dto){
        log.info("添加反馈，反馈内容：{}", dto);
        feedbackService.addFeedback(dto);
        return Result.success();
    }
    @DeleteMapping("/handled")
    public Result<Void> deleteHandledFeedback(){
        log.info("删除已处理的反馈");
        feedbackService.deleteHandledFeedback();
        return Result.success();
    }
}
