package com.createpen.fifteenhundun.module.user.mapper;

import com.createpen.fifteenhundun.common.anno.AutoLog;
import com.createpen.fifteenhundun.common.constant.ModuleNameConstant;
import com.createpen.fifteenhundun.common.constant.OperateTypeConstant;
import com.createpen.fifteenhundun.module.user.pojo.Entity.Feedback;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FeedbackMapper {
    @Select("select * from feedback")
    List<Feedback> selectAllFeedback();
    
    /**
     * 更新反馈状态
     *
     * @param id     反馈ID
     * @param status 新状态
     * @return 影响的行数
     */
    @AutoLog(operateType = OperateTypeConstant.UPDATE, moduleName = ModuleNameConstant.FEEDBACK)
    @Update("update feedback set status = #{status},finished_at = #{now} where id = #{id}")
    int updateFeedbackStatus(Integer id, @NotNull(message = "反馈状态不能为空") Integer status, LocalDateTime now);
}
