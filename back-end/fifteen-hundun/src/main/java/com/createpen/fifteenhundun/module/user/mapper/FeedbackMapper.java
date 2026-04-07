package com.createpen.fifteenhundun.module.user.mapper;

import com.createpen.fifteenhundun.module.user.pojo.Entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedbackMapper {
    @Select("select * from feedback")
    List<Feedback> selectAllFeedback();
}
