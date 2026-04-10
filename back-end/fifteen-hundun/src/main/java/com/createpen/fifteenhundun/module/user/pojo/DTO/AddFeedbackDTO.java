package com.createpen.fifteenhundun.module.user.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddFeedbackDTO {
    @NotBlank(message = "反馈内容不能为空")
    private String type;
    @NotBlank(message = "反馈内容不能为空")
    private String text;

//    private Integer userId;从BaseContext拿到
//    private String userEmail;用um方法
//    private String username;用um方法
    private LocalDateTime createdAt = LocalDateTime.now();
//    private String finishedAt;
//    private String status;
//    private Integer handleUserId;不讲不讲
}


