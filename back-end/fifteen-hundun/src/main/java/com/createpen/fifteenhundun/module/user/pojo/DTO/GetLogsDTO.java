package com.createpen.fifteenhundun.module.user.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志查询DTO（包含分页和多条件筛选）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLogsDTO implements Serializable {
    // 分页参数
    private Integer page; // 页码（从1开始）
    private Integer size; // 每页条数

    // 操作者姓名（模糊查询）
    private String operateUserName;

    // 操作类型（精确/模糊，根据业务需求）
    private String operateType;

    // 操作时长（如：500ms，用于数值比较）
    private String operateTime;

    // 操作是否成功（修正拼写错误：opertrateStatus → operateStatus）
    private String operateStatus;

    // 模块名字（模糊查询）
    private String moduleName;

    // 操作的方法名（模糊查询）
    private String operateFunctionName;

    private Integer status;

    // 操作时间范围-开始（默认值通过构造方法/初始化块设置更优雅）
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    // 操作时间范围-结束
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    // 初始化默认时间（避免直接在字段上赋值导致的序列化问题）
    {
        if (beginTime == null) {
            beginTime = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
    }
}