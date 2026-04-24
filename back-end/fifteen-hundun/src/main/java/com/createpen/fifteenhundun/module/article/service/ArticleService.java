package com.createpen.fifteenhundun.module.article.service;

import com.createpen.fifteenhundun.module.article.pojo.DTO.InsertArticleDTO;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;

import java.util.List;

public interface ArticleService {
    List<Article> selectArticleList();

    void insertArticle(InsertArticleDTO dto);

    void updateArticleGood(Long articleId);
}
