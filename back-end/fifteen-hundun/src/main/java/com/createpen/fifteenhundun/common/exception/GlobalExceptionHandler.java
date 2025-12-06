package com.createpen.fifteenhundun.common.exception;

import com.createpen.fifteenhundun.common.exception.MyException.LoginException;
import com.createpen.fifteenhundun.common.exception.MyException.OSSException;
import com.createpen.fifteenhundun.common.exception.MyException.UserExistedException;
import com.createpen.fifteenhundun.common.exception.MyException.UserNotFoundException;
import com.createpen.fifteenhundun.common.response.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //DTO属性异常处理
    @ExceptionHandler
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.info("DTO接收异常信息:{}",msg);
        return Result.fail(400,msg);
    }

    //jwt相关异常
    @ExceptionHandler
    public Result handleJwtException(ExpiredJwtException e) {
        return Result.fail(401,"令牌已经过期 请重新登录");
    }
    @ExceptionHandler
    public Result handleJwtException(MalformedJwtException e) {
        return Result.fail(401,"令牌无效或者格式有误");
    }

    //用户异常
    @ExceptionHandler
    public Result handleUserNotFoundException(UserNotFoundException e) {
        log.info("已接受到用户不存在异常");
        String msg = e.getMessage();
        return Result.fail(404,msg);
    }
    public Result handleUserExistedException(UserExistedException e) {
        String msg = e.getMessage();
        log.info("已接受到用户已存在异常");
        return Result.fail(409,msg);
    }

    //登录异常
    @ExceptionHandler
    public Result handleLoginException(LoginException e) {
        String msg = e.getMessage();
        log.info("拦截器拦截到登录异常:{}",msg);
        return Result.fail(401,msg);
    }

    //运行时异常处理
    @ExceptionHandler
    public Result handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage();
        log.info("运行时异常信息:{}",msg);
        return Result.fail(500,msg);
    }

    //OSS异常
    @ExceptionHandler
    public Result handleOSSException(OSSException e) {
        String msg = e.getMessage();
        log.info("已接受到OSS异常:{}",msg);
        return Result.fail(500,msg);
    }

//    未知异常处理
    @ExceptionHandler
    public Result handleException(Exception e) {
        log.error(e.getMessage());
        return Result.fail(500,"未知异常");
    }
}
