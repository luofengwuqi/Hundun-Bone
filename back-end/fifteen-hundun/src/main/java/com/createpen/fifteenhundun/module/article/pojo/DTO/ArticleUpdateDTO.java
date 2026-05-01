package com.createpen.fifteenhundun.module.article.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArticleUpdateDTO {
    private Integer articleId;
    private Integer count;
}
