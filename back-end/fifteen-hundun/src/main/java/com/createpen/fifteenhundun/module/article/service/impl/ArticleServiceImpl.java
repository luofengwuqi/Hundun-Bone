package com.createpen.fifteenhundun.module.article.service.impl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.module.article.cache.ArticleRedisService;
import com.createpen.fifteenhundun.module.article.mapper.ArticleMapper;
import com.createpen.fifteenhundun.module.article.pojo.DTO.InsertArticleDTO;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import com.createpen.fifteenhundun.module.article.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.ARTICLE_BAD_COUNT;
import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.ARTICLE_GOOD_COUNT;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRedisService articleRedisService;
    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleRedisService articleRedisService) {
        this.articleMapper = articleMapper;
        this.articleRedisService = articleRedisService;
    }

    @Override
    public List<Article> selectArticleList() {
        List<Article> listInRedis = articleRedisService.selectArticleListInRedis();
        if(listInRedis!=null){
            //查到了 直接返回
            log.info("命中缓存");
            return listInRedis;
        }
        //没查到 在数据库里查 查完放redis里 然后返回
        log.info("未命中缓存");
        List<Article> list = articleMapper.selectArticleList();
        articleRedisService.insertArticleListInRedis(list);
        return list;
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
        if(articleRedisService.isGood(articleId, currentUserId)){
            //1.1已点赞
            //删除点赞状态->更改点赞数-1
            articleRedisService.deleteArticleGoodUser(articleId, currentUserId);
            articleRedisService.updateArticleGoodOrBadCount(ARTICLE_GOOD_COUNT, articleId, -1);
            return;
        }
        //1.2未点赞 两种情况:1 啥都没点 2 点了踩
        //判断点踩状态
        if(articleRedisService.isBad(articleId, currentUserId)){
            //已点踩
            //删除点踩状态
            articleRedisService.deleteArticleBadUser(articleId, currentUserId);
            //更改点踩数-1
            articleRedisService.updateArticleGoodOrBadCount(ARTICLE_BAD_COUNT, articleId, -1);
        }
        //添加点赞状态->更改点赞数+1
        articleRedisService.insertArticleGoodUser(articleId, currentUserId);
        articleRedisService.updateArticleGoodOrBadCount(ARTICLE_GOOD_COUNT, articleId, 1);
        }

    @Override
    public void updateArticleBad(Long articleId) {
        //1判断当前用户点踩状态
        if(articleRedisService.isBad(articleId, BaseContext.getCurrentId())){
            //1.1点踩了 要取消点踩
            articleRedisService.deleteArticleBadUser(articleId, BaseContext.getCurrentId());
            articleRedisService.updateArticleGoodOrBadCount(ARTICLE_BAD_COUNT, articleId, -1);
            return;
        }
        //1.2未点踩 两种情况:1 啥都没点 2 点了赞
        if(articleRedisService.isGood(articleId, BaseContext.getCurrentId())){
            //已点赞 移除点赞 count减一
            articleRedisService.deleteArticleGoodUser(articleId, BaseContext.getCurrentId());
            articleRedisService.updateArticleGoodOrBadCount(ARTICLE_GOOD_COUNT, articleId, -1);
        }
        //添加点赞状态->更改点赞数+1
        articleRedisService.insertArticleBadUser(articleId, BaseContext.getCurrentId());
        articleRedisService.updateArticleGoodOrBadCount(ARTICLE_BAD_COUNT, articleId, 1);
    }
}
