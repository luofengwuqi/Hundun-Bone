package com.createpen.fifteenhundun.module.user.mapper;

import com.createpen.fifteenhundun.common.anno.AutoLog;
import com.createpen.fifteenhundun.common.constant.ModuleNameConstant;
import com.createpen.fifteenhundun.common.constant.OperateTypeConstant;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminMapper {

    @AutoLog(operateType = OperateTypeConstant.SELECT, moduleName = ModuleNameConstant.USER)
    @Select("select count(*) from users")
    Integer getUserNumber();

    @AutoLog(operateType = OperateTypeConstant.SELECT, moduleName = ModuleNameConstant.USER)
    List<User> getUsers(String name, Integer status, LocalDateTime beginTime, LocalDateTime endTime,Integer loginDays);
}
