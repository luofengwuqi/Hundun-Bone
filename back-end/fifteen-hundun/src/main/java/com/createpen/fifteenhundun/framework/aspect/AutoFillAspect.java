package com.createpen.fifteenhundun.framework.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class AutoFillAspect {
//TODO: AOP太他妈难了
    @Pointcut("execution(* com.createpen.fifteenhundun.module..mapper.*(..)) && @annotation(com.createpen.fifteenhundun.common.anno.AutoFillTime)")
    public void autoFillTimePointCut(){}

    @Before("autoFillTimePointCut()")
    public void autoFillTime(JoinPoint joinPoint)
    {
        log.info("自动填充时间");


    }
}
