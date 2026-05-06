package com.createpen.fifteenhundun.module.article.cache;

import com.createpen.fifteenhundun.module.article.mapper.ArticleMapper;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.*;

@Slf4j
@Component
public class ArticleRedisService {

    private final ArticleMapper articleMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public ArticleRedisService(RedisTemplate<String, Object> redisTemplate, ArticleMapper articleMapper) {
        this.redisTemplate = redisTemplate;
        this.articleMapper = articleMapper;
    }
    /*
        文章整体相关
    */
    //在redis查询文章列表
    @SuppressWarnings("unchecked")
    public List<Article> selectArticleListInRedis() {
        try {
            Object o = redisTemplate.opsForValue().get(ARTICLE_LIST);
            if (o == null) {
                log.debug("Redis中未找到文章列表缓存, key: {}", ARTICLE_LIST);
                return null;
            }
            // 安全的类型检查和转换
            if (o instanceof List) {
                return (List<Article>) o;
            } else {
                log.warn("Redis中文章列表数据类型异常, 期望List, 实际类型: {}", o.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            log.error("从Redis查询文章列表失败", e);
            return null;
        }
    }
    //在redis插入文章列表
    public void insertArticleListInRedis(List<Article> list) {
        try{
            log.info("初始化文章列表缓存...");
            redisTemplate.opsForValue().set(ARTICLE_LIST, list,ARTICLE_LIST_EXPIRE, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("插入文章列表缓存失败...");
            throw new RuntimeException("插入文章列表缓存失败...");
        }
    }
    //在redis移除文章列表
    public void deleteArticleListInRedis() {
        try{
            log.info("移除文章列表缓存...");
            redisTemplate.delete(ARTICLE_LIST);
            log.info("已移除文章列表缓存...");
        }
        catch (Exception e){
            log.error("移除文章列表缓存失败...");
            throw new RuntimeException("移除文章列表缓存失败...");
        }
    }



    /*
       文章点赞 点踩相关
    */
    //检查redis中是否有count相关的数据
    public boolean isGoodExist(Long articleId) {
        try{
            return redisTemplate.opsForValue().get(ARTICLE_GOOD_COUNT + articleId) != null;
        }catch (Exception e){
            log.error("查询文章数量失败, articleId: {}", articleId, e);
            throw new RuntimeException("查询文章数量失败...");
        }
    }
    public boolean isBadExist(Long articleId) {
        try{
            return redisTemplate.opsForValue().get(ARTICLE_BAD_COUNT + articleId) != null;
        }catch (Exception e){
            log.error("查询文章数量失败, articleId: {}", articleId, e);
            throw new RuntimeException("查询文章数量失败...");
        }
    }
    //从数据库拿count数据到redis
    public void initArticleGoodOrBadCount(Long id) {
        Article article = articleMapper.selectArticleById(Math.toIntExact(id));
        try{
            redisTemplate.opsForValue().set(ARTICLE_GOOD_COUNT + id, article.getGood());
            redisTemplate.opsForValue().set(ARTICLE_BAD_COUNT + id, article.getBad());
            log.info("已在redis初始化文章点赞数: articleId: {}, good: {}, bad: {}", id, article.getGood(), article.getBad());
        }catch (Exception e){
            log.error("初始化文章点赞数失败, articleId: {}", id, e);
            throw new RuntimeException("初始化文章点赞数失败...");
        }

    }

/*
    下方的代码经过重构已经放置在lua脚本中
    下方的代码存在高并发安全问题/事务无法回滚的问题
    现已弃用
*/

//
//
//    //修改点赞/点踩数量
//    public void updateArticleGoodOrBadCount(String redisConstant,Long articleId, Integer num) {
//        if(num == null || num != 1  && num != -1){
//            log.warn("修改点赞数传参异常...articleId传参为{},count传参为{}",articleId,num);
//            throw new RuntimeException("修改点赞数传参异常...");
//        }
//        //更新redis中点赞数量
//        String key = redisConstant + articleId;
//        if(num == 1){
//            redisTemplate.opsForValue().increment(key, num);
//        }
//        else{//num == -1
//            redisTemplate.opsForValue().decrement(key, -num);
//        }
//    }
////    通用查询文章点赞/差评数量方法
//    public Integer selectArticleGoodOrBadCount(String redisConstant, Long articleId) {
//        try {
//            Object o = redisTemplate.opsForValue().get(redisConstant + articleId);
//            if (o == null) {
//                return null;
//            }
//            // 更安全的类型转换
//            if (o instanceof Integer) {
//                return (Integer) o;
//            } else if (o instanceof Number) {
//                return ((Number) o).intValue();
//            }
//            log.warn("文章数量类型异常, articleId: {}, actualType: {}",
//                     articleId, o.getClass().getName());
//            return null;
//        } catch (Exception e) {
//            log.error("查询文章数量失败, articleId: {}", articleId, e);
//            return null;
//        }
//    }
//
//    //检查用户是否已经点赞文章
//    public Boolean isGood(Long articleId, Long userId) {
//        String key = ARTICLE_GOOD_USER + articleId + ":" + userId;
//        try{
//            return redisTemplate.opsForValue().get(key) != null;
//        }catch (Exception e){
//            log.error("查询用户是否点赞文章失败, articleId: {}, userId: {}", articleId, userId, e);
//            throw new RuntimeException("查询用户是否点赞文章失败...");
//        }
//    }
//    //检查用户是否已经点踩文章
//    public Boolean isBad(Long articleId, Long userId) {
//        String key = ARTICLE_BAD_USER + articleId + ":" + userId;
//        try{
//            return redisTemplate.opsForValue().get(key) != null;
//        }catch (Exception e){
//            log.error("查询用户是否点踩文章失败, articleId: {}, userId: {}", articleId, userId, e);
//            throw new RuntimeException("查询用户是否点赞文章失败...");
//        }
//    }
////    更改用户点赞文章状态:已点过赞
//    public void insertArticleGoodUser(Long articleId, Long userId) {
//        String key = ARTICLE_GOOD_USER + articleId + ":" + userId;
//        try{
//            redisTemplate.opsForValue().set(key, 1);
//        }catch (Exception e){
//            log.error("更改用户点赞文章状态失败, articleId: {}, userId: {}", articleId, userId, e);
//            throw new RuntimeException("更改用户点赞文章状态失败...");
//        }
//    }
//    //更改用户点踩文章状态:已点过踩
//    public void insertArticleBadUser(Long articleId, Long currentId) {
//        String key = ARTICLE_BAD_USER + articleId + ":" + currentId;
//        try{
//            redisTemplate.opsForValue().set(key, 1);
//        }catch (Exception e){
//            log.error("更改用户点踩文章状态失败, articleId: {}, userId: {}", articleId, currentId, e);
//            throw new RuntimeException("更改用户点踩文章状态失败...");
//        }
//    }
//    //更改用户点赞文章状态:未点过赞
//    public void deleteArticleGoodUser(Long articleId, Long userId) {
//        String key = ARTICLE_GOOD_USER + articleId + ":" + userId;
//        try{
//            redisTemplate.delete(key);
//            log.info("已更改用户点赞文章状态:点过赞->未点过赞 articleId: {}, userId: {}", articleId, userId);
//        }catch (Exception e){
//            log.error("更改用户点赞文章状态失败, articleId: {}, userId: {}", articleId, userId, e);
//            throw new RuntimeException("更改文章状态失败...");
//        }
//    }
//
//    //更改用户点赞文章状态:未点过踩
//    public void deleteArticleBadUser(Long articleId, Long currentUserId) {
//        String key = ARTICLE_BAD_USER + articleId + ":" + currentUserId;
//        try{
//            redisTemplate.delete(key);
//            log.info("已更改用户点踩文章状态:点过踩->未点过踩 articleId: {}, userId: {}", articleId, currentUserId);
//        }catch (Exception e){
//            log.error("更改用户点踩文章状态失败, articleId: {}, userId: {}", articleId, currentUserId, e);
//            throw new RuntimeException("更改文章状态失败...");
//        }
//    }





}
