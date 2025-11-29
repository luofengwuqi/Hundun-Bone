package com.createpen.fifteenhundun.common.exception;

import com.createpen.fifteenhundun.common.response.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //jwt相关异常
    @ExceptionHandler
    public Result handleJwtException(ExpiredJwtException e) {
        return Result.fail(401,"令牌已经过期 请重新登录");
    }
    @ExceptionHandler
    public Result handleJwtException(MalformedJwtException e) {
        return Result.fail(401,"令牌无效或者格式有误");
    }

//    未知异常处理
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        return Result.fail(500,"未知异常");
    }
}
