package com.createpen.fifteenhundun.module.user.mapper;

import com.createpen.fifteenhundun.common.anno.AutoFillTime;
import com.createpen.fifteenhundun.common.anno.AutoLog;
import com.createpen.fifteenhundun.common.constant.ModuleNameConstant;
import com.createpen.fifteenhundun.common.constant.OperateTypeConstant;
import com.createpen.fifteenhundun.common.enums.AutoFillTimeType;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
//    User selectByUserNameAndPassword(LoginRequestDTO l);

    //这里不知道加合不合适(太频繁使用了 先不加了)
//    @AutoLog(operateType = OperateTypeConstant.UPDATE, moduleName = ModuleNameConstant.USER)
    User selectByUserName(String username);

    @AutoFillTime(AutoFillTimeType.INSERT)
    @AutoLog(operateType = OperateTypeConstant.INSERT, moduleName = ModuleNameConstant.USER)
    void insertUser(User u);

    @AutoFillTime(AutoFillTimeType.UPDATE)
    @AutoLog(operateType = OperateTypeConstant.UPDATE, moduleName = ModuleNameConstant.USER)
    void updateUserByUserName(User u);

    @Delete("delete from users where username = #{username}")
    @AutoLog(operateType = OperateTypeConstant.DELETE, moduleName = ModuleNameConstant.USER)
    void deleteUserByUserName(String username);


    List<User> selectByUserNameList(List userNameList);

    @AutoLog(operateType = OperateTypeConstant.DELETE, moduleName = ModuleNameConstant.USER)
    void deleteUserByUserNameList(List userNameList);
}
