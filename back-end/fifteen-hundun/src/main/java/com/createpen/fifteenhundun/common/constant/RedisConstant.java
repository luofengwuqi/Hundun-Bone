package com.createpen.fifteenhundun.common.constant;


public class RedisConstant {
    //key   user:token:${userId}
    //value token
    public static final String TOKEN_PATH = "user:token:";
    //30个单位时间后自动过期(三十分钟)
    public static final Integer TOKEN_EXPIRE_TIME = 30;
}
