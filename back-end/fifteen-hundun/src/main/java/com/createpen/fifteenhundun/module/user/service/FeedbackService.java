package com.createpen.fifteenhundun.module.user.service;

import com.createpen.fifteenhundun.module.user.pojo.Entity.Feedback;

import java.util.List;

public interface FeedbackService {
    List<Feedback> selectAllFeedback();
}
