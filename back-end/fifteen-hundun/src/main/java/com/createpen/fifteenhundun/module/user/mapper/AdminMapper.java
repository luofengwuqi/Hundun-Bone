package com.createpen.fifteenhundun.module.user.mapper;

import com.createpen.fifteenhundun.common.anno.AutoLog;
import com.createpen.fifteenhundun.common.constant.ModuleNameConstant;
import com.createpen.fifteenhundun.common.constant.OperateTypeConstant;
import com.createpen.fifteenhundun.module.user.pojo.DTO.GetLogsDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.OperateLog;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminMapper {

    @AutoLog(operateType = OperateTypeConstant.SELECT, moduleName = ModuleNameConstant.ADMIN)
    @Select("select count(*) from users")
    Integer getUserNumber();

    @AutoLog(operateType = OperateTypeConstant.SELECT, moduleName = ModuleNameConstant.ADMIN)
    List<User> getUsers(String name, Integer status, LocalDateTime beginTime, LocalDateTime endTime,Integer loginDays);

    //已被弃用
//    @AutoLog(operateType = OperateTypeConstant.DELETE, moduleName = ModuleNameConstant.ADMIN)
//    @Delete("delete from users where username = #{username}")
//    void deleteUserByUserName(String username);

    @Select("select invite_code from users where invite_code = #{importantPassword}")
    String selectInvitePassword(@NotBlank(message = "邀请码不能为空") String importantPassword);

    //我也不知道这里适不适合加自动填充注解(我觉得还是算了
    @AutoLog(operateType = OperateTypeConstant.SELECT, moduleName = ModuleNameConstant.ADMIN)
    List<OperateLog> getLogs(GetLogsDTO dto);
}
