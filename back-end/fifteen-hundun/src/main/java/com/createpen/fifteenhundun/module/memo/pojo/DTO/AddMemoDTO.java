package com.createpen.fifteenhundun.module.memo.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemoDTO {
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100位")
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
    @NotNull(message = "主类型不能为空")
    private Integer mainType;

    /**
     * 子类型：1-人生警示，2-重要数据（仅main_type=1时有效）
     */
    private Integer subType;

    /**
     * 关联用户ID（前端不传，从Token解析后注入）
     */
    private Long userId;

}

