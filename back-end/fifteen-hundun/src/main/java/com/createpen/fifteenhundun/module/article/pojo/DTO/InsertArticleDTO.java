package com.createpen.fifteenhundun.module.article.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class InsertArticleDTO {
    private String title;
    private String text;
    private String creator;
    private Integer belonging;
}
