package com.createpen.fifteenhundun.framework.aspect;

import com.createpen.fifteenhundun.common.anno.AutoLog;
import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.module.user.mapper.CommonMapper;
import com.createpen.fifteenhundun.module.user.pojo.Entity.OperateLog;
import org.springframework.web.context.request.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
@Aspect
public class AutoLogAspect {

    @Autowired
    private CommonMapper cm;


     @Pointcut("execution(* com.createpen.fifteenhundun.module.*.mapper.*.*(..)) && @annotation(com.createpen.fifteenhundun.common.anno.AutoLog)")
     public void autoLogPointCut(){}

     @Around("autoLogPointCut()")
     public Object autoLog(ProceedingJoinPoint pjp) throws Throwable
     {
         log.info("自动记录日志启动");
         //初始化日志对象
         OperateLog o = new OperateLog();
         o.setOperateUserId(BaseContext.getCurrentId());
         o.setOperateUserName(BaseContext.getCurrentUsername());
         o.setOperateAt(LocalDateTime.now());

         // 2. 获取请求上下文（IP、用户ID等）
         ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
         if (Objects.nonNull(attributes)) {
             HttpServletRequest request = attributes.getRequest();
             // 获取操作IP
             o.setIpAddress(getIpAddress(request));
         }

         // 3. 获取注解里的信息（操作类型、模块名）
         MethodSignature signature = (MethodSignature) pjp.getSignature();
         AutoLog annotation = signature.getMethod().getAnnotation(AutoLog.class);
         if (Objects.nonNull(annotation)) {
             o.setOperateType(annotation.operateType());
             o.setModuleName(annotation.moduleName());
             o.setOperateFunctionName(pjp.getSignature().getName());
         }

         Long begin = System.currentTimeMillis();
         try {
             Object result = pjp.proceed();
//             log.info("\n____________________执行成功______________________");
             o.setOperateStatus(1);
             return result;

         } catch (Throwable e) {
//             log.info("\n____________________执行失败______________________");
//             o.setOperateStatus(0);
//             throw e;
//             o.setResponseResult("执行失败：" + e.getMessage());
             throw new RuntimeException(e);
         }finally {
             Long end = System.currentTimeMillis();
             o.setOperateTime(end - begin + "ms");
             //写入日志
             log.info("已写入日志{}",o );
             cm.addLog(o);
         }

     }
    // 工具方法：获取真实IP

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
