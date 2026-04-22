package com.createpen.fifteenhundun.module.article.controller;

import com.createpen.fifteenhundun.common.response.Result;
import com.createpen.fifteenhundun.module.article.pojo.DTO.InsertArticleDTO;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import com.createpen.fifteenhundun.module.article.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public Result<List<Article>> selectArticleList() {
        List<Article> list = articleService.selectArticleList();
        return Result.success(list);
    }
    @PostMapping
    public Result<Void> insertArticle(@RequestBody InsertArticleDTO dto) {
        log.info("insertArticle: {}", dto);
        articleService.insertArticle(dto);
        return Result.success();
    }
}
