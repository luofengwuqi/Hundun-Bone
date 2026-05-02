package com.createpen.fifteenhundun.module.article.service.impl;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.module.article.cache.ArticleGoodBadRedisService;
import com.createpen.fifteenhundun.module.article.cache.ArticleRedisService;
import com.createpen.fifteenhundun.module.article.cache.ArticleRedisToMysqlService;
import com.createpen.fifteenhundun.module.article.mapper.ArticleMapper;
import com.createpen.fifteenhundun.module.article.pojo.DTO.InsertArticleDTO;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import com.createpen.fifteenhundun.module.article.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.createpen.fifteenhundun.module.article.pojo.DTO.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.*;


@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleGoodBadRedisService articleGoodBadRedisService;

    private final ArticleRedisToMysqlService rTmService;
    private final ArticleRedisService articleRedisService;
    private final ArticleMapper articleMapper;


    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleRedisService articleRedisService, ArticleRedisToMysqlService rTmService, ArticleGoodBadRedisService articleGoodBadRedisService) {
        this.articleMapper = articleMapper;
        this.articleRedisService = articleRedisService;
        this.rTmService = rTmService;
        this.articleGoodBadRedisService = articleGoodBadRedisService;
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

    /*
        下面的两个方法只操作redis里面的点赞/点踩相关的东西
        //todo:未解决点踩高并发下事务原子性问题
    */
    @Override
    public void updateArticleGood(Long articleId) {
//        Long currentUserId = BaseContext.getCurrentId();
//        //1 判断当前状态
//        if(articleRedisService.isGood(articleId, currentUserId)){
//            //1.1已点赞
//            //删除点赞状态->更改点赞数-1
//            articleRedisService.deleteArticleGoodUser(articleId, currentUserId);
//            articleRedisService.updateArticleGoodOrBadCount(ARTICLE_GOOD_COUNT, articleId, -1);
//            return;
//        }
//        //1.2未点赞 两种情况:1 啥都没点 2 点了踩
//        //判断点踩状态
//        if(articleRedisService.isBad(articleId, currentUserId)){
//            //已点踩
//            //删除点踩状态
//            articleRedisService.deleteArticleBadUser(articleId, currentUserId);
//            //更改点踩数-1
//            articleRedisService.updateArticleGoodOrBadCount(ARTICLE_BAD_COUNT, articleId, -1);
//        }
//        //添加点赞状态->更改点赞数+1
//        articleRedisService.insertArticleGoodUser(articleId, currentUserId);
//        articleRedisService.updateArticleGoodOrBadCount(ARTICLE_GOOD_COUNT, articleId, 1);

        //检查redis里面是否存过count相关数据了 如果没存就从数据库初始化
        if(!articleRedisService.isGoodExist(articleId) || !articleRedisService.isBadExist(articleId)){
            articleRedisService.initArticleGoodOrBadCount(articleId);
        }
        //执行lua点赞脚本
        Long result = articleGoodBadRedisService.doGoodScript(articleId, BaseContext.getCurrentId());
        log.info("用户 {} 对文章 {} 点赞操作结果: {}", BaseContext.getCurrentId(), articleId, result == 1 ? "已点赞" : "已取消点赞");
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



    /*
    这里是一个比较复杂的事务
    用途是移除redis里面的点赞/点踩数 并同步到数据库
    */
    @Override
    @Transactional
    //为每晚2:00 定时服务提供的方法
    public void syncRedisToMysqlForCount() {
        //1 抽取方法
        syncRedisToMysqlForGoodCount();
        syncRedisToMysqlForBadCount();
        // 2. 注册事务提交后的回调
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // 事务提交成功后才执行
                        try {
                            rTmService.deleteAllArticleCount();
                            log.info("Redis 计数清空成功");
                        } catch (Exception e) {
                            log.error("Redis 清空失败，可能导致下次重复同步", e);
                            // 这里可以发送告警或记录到监控表
                        }
                    }});
        log.info("同步点赞/点踩数成功");

    }
    //拆分方法
    private void syncRedisToMysqlForGoodCount(){
        Map<Integer,Integer> likeCountMap = rTmService.selectAllArticleGoodCount();
        if(likeCountMap==null || likeCountMap.isEmpty()){
            log.warn("没有检查到有文章变更点赞数?");
            return;
        }
        log.info("获取到有记录的点赞集合:{}",likeCountMap);
        try{
            articleMapper.batchUpdateArticleGoodCount(mapToList(likeCountMap));
            log.info("同步点赞数成功");
        }catch(Exception e){
            log.error("同步点赞到mysql数失败:{}",e.getMessage());
            throw new RuntimeException("同步点赞数到mysql失败");
        }
    }
    //拆分方法

    private void syncRedisToMysqlForBadCount(){
        //1 从redis获取数据

        Map<Integer,Integer> badCountMap = rTmService.selectAllArticleBadCount();
        if(badCountMap==null || badCountMap.isEmpty()){
            log.warn("没有检查到有文章变更点踩数?");
            return;
        }
        log.info("获取到有记录的点踩集合:{}",badCountMap);
        //2 操作mysql
        try{
            articleMapper.batchUpdateArticleBadCount(mapToList(badCountMap));
        }catch(Exception e){
            log.error("同步点踩数到mysql失败:{}",e.getMessage());
            throw new RuntimeException("同步点踩数到mysql失败");
        }
    }

    //辅助方法Map<Integer,Integer>->List<ArticleUpdateDTO>
    private List<ArticleUpdateDTO> mapToList(Map<Integer,Integer> map){
        if(map==null){
            return new ArrayList<>();
        }
        List<ArticleUpdateDTO> list = new ArrayList<>();
        for(Map.Entry<Integer,Integer> entry:map.entrySet()){
            ArticleUpdateDTO dto = new ArticleUpdateDTO();
            dto.setArticleId(entry.getKey());
            dto.setCount(entry.getValue());
            list.add(dto);
        }
        return list;
    }

    /*    todo:
    这里是一个比较复杂的事务
    用途是记录redis里面的点赞关系表到数据库
    */


}
