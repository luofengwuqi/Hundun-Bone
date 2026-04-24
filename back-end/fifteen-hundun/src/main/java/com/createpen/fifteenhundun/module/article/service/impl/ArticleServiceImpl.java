package com.createpen.fifteenhundun.module.article.service.impl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.util.Redis.ArticleRedisUtil;
import com.createpen.fifteenhundun.module.article.mapper.ArticleMapper;
import com.createpen.fifteenhundun.module.article.pojo.DTO.InsertArticleDTO;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import com.createpen.fifteenhundun.module.article.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.ARTICLE_BAD_COUNT;
import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.ARTICLE_GOOD_COUNT;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRedisUtil articleRedisUtil;
    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleRedisUtil articleRedisUtil) {
        this.articleMapper = articleMapper;
        this.articleRedisUtil = articleRedisUtil;
    }

    @Override
    public List<Article> selectArticleList() {
        return articleMapper.selectArticleList();
    }

    @Override
    public void insertArticle(InsertArticleDTO dto) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        article.setCreatedAt(LocalDateTime.now());
        articleMapper.insertArticle(article);
    }

    @Override
    public void updateArticleGood(Long articleId) {
        Long currentUserId = BaseContext.getCurrentId();
        //1 判断当前状态
        if(articleRedisUtil.isGood(articleId, currentUserId)){
            //1.1已点赞
            //删除点赞状态->更改点赞数-1
            articleRedisUtil.deleteArticleGoodUser(articleId, currentUserId);
            articleRedisUtil.updateArticleGoodOrBadCount(ARTICLE_GOOD_COUNT, articleId, -1);
            return;
        }
        //1.2未点赞 两种情况:1 啥都没点 2 点了踩
        //判断点踩状态
        if(articleRedisUtil.isBad(articleId, currentUserId)){
            //已点踩
            //删除点踩状态
            articleRedisUtil.deleteArticleBadUser(articleId, currentUserId);
            //更改点踩数-1
            articleRedisUtil.updateArticleGoodOrBadCount(ARTICLE_BAD_COUNT, articleId, -1);
        }
        //添加点赞状态->更改点赞数+1
        articleRedisUtil.insertArticleGoodUser(articleId, currentUserId);
        articleRedisUtil.updateArticleGoodOrBadCount(ARTICLE_GOOD_COUNT, articleId, 1);
        }
    }
