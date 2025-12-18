package com.createpen.fifteenhundun.module.user.mapper;

import com.createpen.fifteenhundun.common.anno.AutoFillTime;
import com.createpen.fifteenhundun.common.anno.AutoLog;
import com.createpen.fifteenhundun.common.constant.ModuleNameConstant;
import com.createpen.fifteenhundun.common.constant.OperateTypeConstant;
import com.createpen.fifteenhundun.common.enums.AutoFillTimeType;
import com.createpen.fifteenhundun.module.user.pojo.DTO.LoginRequestDTO;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.pojo.VO.LoginRequestVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
//    User selectByUserNameAndPassword(LoginRequestDTO l);

    User selectByUserName(String username);

    @AutoFillTime(AutoFillTimeType.INSERT)
    void insertUser(User u);

    @AutoFillTime(AutoFillTimeType.UPDATE)
    void updateUserByUserName(User u);

    @Delete("delete from users where username = #{username}")
    void deleteUserByUserName(String username);
}
