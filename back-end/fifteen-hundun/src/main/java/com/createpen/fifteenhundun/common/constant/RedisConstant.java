package com.createpen.fifteenhundun.common.constant;


public class RedisConstant {
    //token:${token}
    public static final String TOKEN_PATH = "token:";
    //30 个单位时间后自动过期 (三十分钟)
    public static final Integer TOKEN_EXPIRE_TIME = 30;
    
    //用户查询缓存前缀
    public static final String USERS_CACHE_PATH = "admin:users:";
    //用户查询缓存过期时间（5 分钟）
    public static final Long USERS_CACHE_EXPIRE_TIME = 5L;

    //反馈查询缓存(不想写条件查询 直接当成一个整的 )
    public static final String FEEDBACK_CACHE_PATH = "admin:feedback:all";
    //反馈查询缓存过期时间（5 分钟）
    public static final Long FEEDBACK_CACHE_EXPIRE_TIME = 5L;

    //邮箱验证码(user:email:code:${email})
    public static final String EMAIL_CODE_PATH = "user:email:code:";
    //5分钟过期
    public static final long EMAIL_CODE_EXPIRE_TIME = 5;
}
