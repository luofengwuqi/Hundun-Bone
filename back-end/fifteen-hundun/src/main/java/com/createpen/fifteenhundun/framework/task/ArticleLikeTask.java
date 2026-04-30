package com.createpen.fifteenhundun.framework.task;

import com.createpen.fifteenhundun.module.article.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ArticleLikeTask {
    private final ArticleService articleService;

    public ArticleLikeTask(ArticleService articleService) {
        this.articleService = articleService;
    }


    //执行时机:每天凌晨两点
    @Scheduled(cron = "0 0 2 * * ?")
//            misfirePolicy = "EXECUTE_SINGLE" 版本不兼容 已经移除
    public void executeTask()
    {
        log.info("开始自动执行task任务:移除redis中的文章点赞点踩数据相关的缓存 主动写入数据库");
        articleService.syncRedisToMysql();
        log.info("完成自动执行task任务:移除redis中的文章点赞点踩数据相关的缓存 主动写入数据库");
    }
}
