package com.createpen.fifteenhundun.module.article.pojo.Entity;

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
    private LocalDateTime createdAt;
    private Integer good;
    private Integer bad;
}