package com.createpen.fifteenhundun.common.constant;


public class RedisConstant {
    //token:${token}
    public static final String TOKEN_PATH = "token:";
    //30 个单位时间后自动过期 (三十分钟)
    public static final Integer TOKEN_EXPIRE_TIME = 3;
    
    //用户查询缓存前缀
    public static final String USERS_CACHE_PATH = "admin:users:";
    //用户查询缓存过期时间（5 分钟）
    public static final Long USERS_CACHE_EXPIRE_TIME = 5L;
}
