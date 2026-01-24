package com.createpen.fifteenhundun.framework.aspect;


import com.createpen.fifteenhundun.common.anno.AutoFillTime;
import com.createpen.fifteenhundun.common.enums.AutoFillTimeType;
//import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Component
@Aspect
public class AutoFillAspect {
//TODOed: AOP太(脏话)难了
    @Pointcut("execution(* com.createpen.fifteenhundun.module.*.mapper.*.*(..)) && @annotation(com.createpen.fifteenhundun.common.anno.AutoFillTime)")
    public void autoFillTimePointCut(){}

    //不是我不想写注释 是因为我真搞不太明白
    @Before("autoFillTimePointCut()")
    public void autoFillTime(JoinPoint joinPoint)
    {
        log.info("自动填充时间启动");
        //获取方法参数
        AutoFillTimeType value = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(AutoFillTime.class).value();
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0)
        {
            return;
        }
        Object entity = args[0];
        LocalDateTime now = LocalDateTime.now();
        if(value == AutoFillTimeType.UPDATE)
        {
            log.info("UPDATE");
            try {
                Method setUpdatedAt = entity.getClass().getDeclaredMethod("setUpdatedAt", LocalDateTime.class);
                setUpdatedAt.invoke(entity,now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("已经填充时间");
        }
        if(value == AutoFillTimeType.INSERT)
        {
            log.info("INSERT");
            try {
                Method setCreatedAt = entity.getClass().getDeclaredMethod("setCreatedAt", LocalDateTime.class);
                Method setUpdatedAt = entity.getClass().getDeclaredMethod("setUpdatedAt", LocalDateTime.class);
                setCreatedAt.invoke(entity,now);
                setUpdatedAt.invoke(entity,now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
