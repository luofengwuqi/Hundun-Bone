package com.createpen.fifteenhundun.module.user.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Feedback {
    private Integer id;
    private String type;
    private String text;
    private Integer userId;
    private String userEmail;
    private String username;
    private String createdAt;
    private String finishedAt;
    private String status;
    private Integer handleUserId;
}
