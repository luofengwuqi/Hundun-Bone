package com.createpen.fifteenhundun.common.util.Redis;

import com.createpen.fifteenhundun.module.article.mapper.ArticleMapper;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.*;

@Slf4j
@Component
public class ArticleRedisUtil {

    private final ArticleMapper articleMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public ArticleRedisUtil(RedisTemplate<String, Object> redisTemplate, ArticleMapper articleMapper) {
        this.redisTemplate = redisTemplate;
        this.articleMapper = articleMapper;
    }



    //修改点赞/点踩数量
    public void updateArticleGoodOrBadCount(String redisConstant,Long articleId, Integer num) {
        if(num == null){
            log.warn("修改点赞数传参异常...articleId传参为{},count传参为{}",articleId,num);
            throw new RuntimeException("修改点赞数传参异常...");
        }
//        try{
            //从redis中获取点赞数量
            Integer count = selectArticleGoodOrBadCount(redisConstant, articleId);
            // 如果count为null,说明缓存未初始化,先从数据库加载
            if (count == null) {
                initArticleGoodOrBadCount(Math.toIntExact(articleId));
                count =  selectArticleGoodOrBadCount(redisConstant, articleId);
            }
            if( count == null){
                log.warn("修改点赞数传参异常...articleId传参为{},count传参为{}",articleId,count);
                return;
            }
            if(count + num < 0){
                log.warn("修改点赞数传参异常...articleId传参为{},当前数量为{},变化量为{}",articleId,count,num);
                return;
            }
            //更新redis中点赞数量
            String key = redisConstant + articleId;
            redisTemplate.opsForValue().set(key, count + num);
//        }catch (Exception e){
//            log.error("修改文章数量失败, articleId: {}, num: {}", articleId, num, e);
//        }
    }



    //通用查询文章点赞/差评数量方法
    private Integer selectArticleGoodOrBadCount(String redisConstant, Long articleId) {
        try {
            Object o = redisTemplate.opsForValue().get(redisConstant + articleId);
            if (o == null) {
                return null;
            }
            // 更安全的类型转换
            if (o instanceof Integer) {
                return (Integer) o;
            } else if (o instanceof Number) {
                return ((Number) o).intValue();
            }
            log.warn("文章数量类型异常, articleId: {}, actualType: {}",
                     articleId, o.getClass().getName());
            return null;
        } catch (Exception e) {
            log.error("查询文章数量失败, articleId: {}", articleId, e);
            return null;
        }
    }

    //检查用户是否已经点赞文章
    public Boolean isGood(Long articleId, Long userId) {
        String key = ARTICLE_GOOD_USER + articleId + ":" + userId;
        try{
            return redisTemplate.opsForValue().get(key) != null;
        }catch (Exception e){
            log.error("查询用户是否点赞文章失败, articleId: {}, userId: {}", articleId, userId, e);
            throw new RuntimeException("查询用户是否点赞文章失败...");
        }
    }
    //检查用户是否已经点踩文章
    public Boolean isBad(Long articleId, Long userId) {
        String key = ARTICLE_BAD_USER + articleId + ":" + userId;
        try{
            return redisTemplate.opsForValue().get(key) != null;
        }catch (Exception e){
            log.error("查询用户是否点踩文章失败, articleId: {}, userId: {}", articleId, userId, e);
            throw new RuntimeException("查询用户是否点赞文章失败...");
        }
    }
    //更改用户点赞文章状态:已点过赞
    public void insertArticleGoodUser(Long articleId, Long userId) {
        String key = ARTICLE_GOOD_USER + articleId + ":" + userId;
        try{
            redisTemplate.opsForValue().set(key, 1);
        }catch (Exception e){
            log.error("更改用户点赞文章状态失败, articleId: {}, userId: {}", articleId, userId, e);
            throw new RuntimeException("更改用户点赞文章状态失败...");
        }
    }
    //更改用户点赞文章状态:未点过赞
    public void deleteArticleGoodUser(Long articleId, Long userId) {
        String key = ARTICLE_GOOD_USER + articleId + ":" + userId;
        try{
            redisTemplate.delete(key);
            log.info("已更改用户点赞文章状态:点过赞->未点过赞 articleId: {}, userId: {}", articleId, userId);
        }catch (Exception e){
            log.error("更改用户点赞文章状态失败, articleId: {}, userId: {}", articleId, userId, e);
            throw new RuntimeException("更改文章状态失败...");
        }
    }

    //更改用户点赞文章状态:未点过踩

    public void deleteArticleBadUser(Long articleId, Long currentUserId) {
        String key = ARTICLE_BAD_USER + articleId + ":" + currentUserId;
        try{
            redisTemplate.delete(key);
            log.info("已更改用户点踩文章状态:点过踩->未点过踩 articleId: {}, userId: {}", articleId, currentUserId);
        }catch (Exception e){
            log.error("更改用户点踩文章状态失败, articleId: {}, userId: {}", articleId, currentUserId, e);
            throw new RuntimeException("更改文章状态失败...");
        }
    }

    //辅助方法
    private void initArticleGoodOrBadCount(Integer id) {
        Article article = articleMapper.selectArticleById(id);
        try{
            redisTemplate.opsForValue().set(ARTICLE_GOOD_COUNT + id, article.getGood());
            redisTemplate.opsForValue().set(ARTICLE_BAD_COUNT + id, article.getBad());
            log.info("已在redis初始化文章点赞数: articleId: {}, good: {}, bad: {}", id, article.getGood(), article.getBad());
        }catch (Exception e){
            log.error("初始化文章点赞数失败, articleId: {}", id, e);
            throw new RuntimeException("初始化文章点赞数失败...");
        }

    }
}

//辅助方法

