package com.createpen.fifteenhundun.module.article.service.impl;

import com.createpen.fifteenhundun.module.article.mapper.ArticleMapper;
import com.createpen.fifteenhundun.module.article.pojo.DTO.InsertArticleDTO;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import com.createpen.fifteenhundun.module.article.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public List<Article> selectArticleList() {
        return articleMapper.selectArticleList();
    }

    @Override
    public void insertArticle(InsertArticleDTO dto) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        articleMapper.insertArticle(article);
    }


}
