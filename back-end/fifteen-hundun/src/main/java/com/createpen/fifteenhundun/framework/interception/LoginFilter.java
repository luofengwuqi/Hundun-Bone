package com.createpen.fifteenhundun.framework.interception;

import com.createpen.fifteenhundun.common.exception.MyException.LoginException;
import com.createpen.fifteenhundun.common.util.JwtUtil;
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
            chain.doFilter(request, response);
            log.info("已经放行");
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
        try{
            JwtUtil.solveToken(token);
        }catch (Exception e)
        {
            log.info("解析token失败");
            res.setStatus(401);
            res.setContentType("application/json;charset=utf-8");
            res.getWriter().write("{\"code\":401,\"message\":\"登录已过期\",\"data\":null}");
            return;
        }

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {
    }
    @Override
    public void init(FilterConfig config) throws ServletException {
    }
}
