package com.createpen.fifteenhundun.module.memo.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 备忘实体类（纯MyBatis版本）
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Memo {
    /**
     * 备忘ID
     */
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 备忘标题
     */
    private String title;

    /**
     * 小内容直接存储
     */
    private String content;

    /**
     * 内容文件路径（大内容时使用）
     */
    private String contentPath;

    /**
     * 主类型：1-必须铭记，2-日常规划
     */
    private Integer mainType;

    /**
     * 子类型：1-人生警示，2-重要数据（仅main_type=1时有效）
     */
    private Integer subType;

    /**
     * 状态：1-正常，0-删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}