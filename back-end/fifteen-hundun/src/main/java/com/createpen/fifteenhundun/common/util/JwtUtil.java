package com.createpen.fifteenhundun.common.util;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.Map;

//todoed:完成JWT工具类书写
public class JwtUtil {
    //秘钥:一五馄饨creatpen
    private static final String SECRET_KEY = "5LiA5LqU6aaE6aWoY3JlYXRwZW4=";
    //过期时间:24小时
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    //生成令牌
    public static String makeToken(Map<String,Object> claims)
    {
         return Jwts.builder().signWith(SignatureAlgorithm.HS256,SECRET_KEY)
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .compact();
    }
    public static String makeToken(Integer id,String username)
    {
        return makeToken(Map.of("id",id,"username",username));
    }


    //解析令牌
    public static Claims solveToken(String token) throws ExpiredJwtException,MalformedJwtException
    {
        return Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
