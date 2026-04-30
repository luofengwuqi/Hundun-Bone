//package com.createpen.fifteenhundun.framework.task;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//
////这个类的作用是在启动服务器的时候运行一些可能被错过的springTask代码
//@Slf4j
//@Component
//public class StartRunner implements ApplicationRunner {
//    private final ArticleLikeTask articleLikeTask;
//
//    public StartRunner(ArticleLikeTask articleLikeTask) {
//        this.articleLikeTask = articleLikeTask;
//    }
//
//    @Override
//    public void run(ApplicationArguments args)  {
//        log.info("ApplicationRunner:启动服务器的时候运行一些可能被错过的springTask代码");
//        //2:00 a.m
//        articleLikeTask.executeTask();
//    }
//}

//todo:暂时禁用了启动时执行的task