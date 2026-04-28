package com.createpen.fifteenhundun.module.article.pojo.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章表实体类
 * 对应数据库表：article
 */
@Data
public class Article {
    private Integer id;
    private String title;
    private String text;
    private Integer status;
    private String creator;
    private Integer belonging;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
    private Integer good;
    private Integer bad;
}