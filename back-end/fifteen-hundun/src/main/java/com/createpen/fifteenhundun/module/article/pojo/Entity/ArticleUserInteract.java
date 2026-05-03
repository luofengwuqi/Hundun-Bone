package com.createpen.fifteenhundun.module.article.pojo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArticleUserInteract {
    private Long userId;
    private Long articleId;
    private Integer interactType;
}
