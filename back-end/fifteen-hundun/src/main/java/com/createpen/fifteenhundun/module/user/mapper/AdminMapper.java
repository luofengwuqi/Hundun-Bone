package com.createpen.fifteenhundun.module.user.mapper;

import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("select count(*) from users")
    Integer getUserNumber();

    List<User> getUsers(String name, Integer status, LocalDateTime beginTime, LocalDateTime endTime,Integer loginDays);
}
