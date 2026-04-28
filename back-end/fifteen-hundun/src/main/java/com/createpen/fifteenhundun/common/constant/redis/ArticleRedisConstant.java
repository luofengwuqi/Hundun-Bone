package com.createpen.fifteenhundun.common.constant.redis;

public class ArticleRedisConstant {
    //所有文章的缓存article:list
    public static final String ARTICLE_LIST = "article:list";
    //所有文章缓存的过期时间
    public static final Integer ARTICLE_LIST_EXPIRE = 30;
    //用户点赞文章缓存 article:good:user:${articleId}:${userId}
    public static final String ARTICLE_GOOD_USER = "article:good:user:";
    //文章点赞数缓存 article:good:count:${articleId}
    public static final String ARTICLE_GOOD_COUNT = "article:good:count:";
    //用户点踩文章缓存 article:bad:user:${articleId}:${userId}
    public static final String ARTICLE_BAD_USER = "article:bad:user:";
    //文章点踩数缓存 article:bad:count:${articleId}
    public static final String ARTICLE_BAD_COUNT = "article:bad:count:";


}
