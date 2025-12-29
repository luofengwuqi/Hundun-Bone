package com.createpen.fifteenhundun.framework.interception;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.exception.MyException.LoginException;
import com.createpen.fifteenhundun.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("进入过滤器");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        if(uri.contains("/login"))
        {
            log.info("检查到登录请求");
            BaseContext.setCurrentUsername("login");
            BaseContext.setCurrentId(0L);
            chain.doFilter(request, response);
            return;
        }
        String token = req.getHeader("token");
        if(token==null || token.isEmpty())
        {
            log.info("检查到token为空");
            res.setStatus(401);
            res.setContentType("application/json;charset=utf-8");
            res.getWriter().write("{\"code\":401,\"message\":\"未登录\",\"data\":null}");
            return;
        }
        //创建解析令牌得到的信息对象 备用
        Claims c;
        try{
            c = JwtUtil.solveToken(token);
        }catch (Exception e)
        {
            log.info("解析token失败");
            res.setStatus(401);
            res.setContentType("application/json;charset=utf-8");
            res.getWriter().write("{\"code\":401,\"message\":\"登录已过期\",\"data\":null}");
            return;
        }
        log.info("已经通过校验");
//        这里是不聪明的办法 小朋友们不要学习
//        System.out.println(c);
//        String idMsg =  c.toString().split(",")[0];
//        System.out.println(idMsg);
//        String id = idMsg.split("=")[1];
//         从 Claims 中提取用户ID和用户名
//        System.out.println(id);
        log.info("现在在往线程里面塞东西");
        Integer id = c.get("id", Integer.class);  // 假设token中存储了id字段
        String username = c.get("username", String.class);
        //把拿到的id成功塞进线程里面
        BaseContext.setCurrentId(Long.valueOf(id));
        BaseContext.setCurrentUsername(username);

        chain.doFilter(request, response);
        log.info("已经放行");


    }

    @Override
    public void destroy() {
    }
    @Override
    public void init(FilterConfig config) throws ServletException {
    }
}
