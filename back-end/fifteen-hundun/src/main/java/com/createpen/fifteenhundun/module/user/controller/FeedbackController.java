package com.createpen.fifteenhundun.module.user.controller;

import com.createpen.fifteenhundun.common.response.Result;
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
        log.info("更新反馈状态，反馈ID：{}，新状态：{}", dto.getId(), dto.getStatus());
        feedbackService.updateFeedbackStatus(dto);
        return Result.success();
    }
}
