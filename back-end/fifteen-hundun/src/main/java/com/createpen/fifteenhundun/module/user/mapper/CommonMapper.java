package com.createpen.fifteenhundun.module.user.mapper;

import com.createpen.fifteenhundun.module.user.pojo.Entity.OperateLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonMapper {
    //自动写入日志最终调用的方法
    void addLog(OperateLog o);
}
