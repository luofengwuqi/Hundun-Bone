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

    //查询所有文章
    @GetMapping
    public Result<List<Article>> selectArticleList() {
        List<Article> list = articleService.selectArticleList();
        return Result.success(list);
    }
    //上传文章
    @PostMapping
    public Result<Void> insertArticle(@RequestBody InsertArticleDTO dto) {
        log.info("insertArticle: {}", dto);
        articleService.insertArticle(dto);
        return Result.success();
    }
    //修改文章点赞/点踩状态
    @PostMapping("/good/{id}")
    public Result<Void> updateArticleGood(@PathVariable Long id) {
        log.info("updateArticleGood: {}", id);
        articleService.updateArticleGood(id);
        return Result.success();
    }
    @PostMapping("/bad/{id}")
    public Result<Void> updateArticleBad(@PathVariable Long id) {
        log.info("updateArticleBad: {}", id);
        articleService.updateArticleBad(id);
        return Result.success();
    }

    @PostMapping("/sync")
    public Result<Void> syncRedisToMysql() {
        log.info("syncRedisToMysql");
        articleService.syncRedisToMysqlForCount();
        articleService.syncRedisToMysqlForArticleGoodUser();
        articleService.syncRedisToMysqlForArticleBadUser();
        return Result.success();
    }
}
