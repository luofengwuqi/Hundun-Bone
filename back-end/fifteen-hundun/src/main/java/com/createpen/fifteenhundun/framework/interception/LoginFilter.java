package com.createpen.fifteenhundun.framework.interception;

import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * 登录校验过滤器（修复核心问题：放行注册接口、规范响应、处理空指针、避免重复注册）
 */
@Slf4j
@Component // 仅用Spring管理，去掉@WebFilter避免重复注册
public class LoginFilter implements Filter {

    //预先声明直接放行的请求
    private static final List<String> SKIP_URIS = Arrays.asList(
            "/users/auth/login",//登录接口
            "/users"       //注册接口
    );
    //对应SKIP_URIS中需要限制方法的路径（索引对应
    private static final List<String> SKIP_URIS_METHODS = Arrays.asList(
            "",                   // 登录接口：允许所有方法
            "POST"                // 注册接口：仅允许POST
    );
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("进入登录校验过滤器");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        String method = req.getMethod();
        log.info("当前请求URI：{}", uri);
        log.info("当前请求方法：{}", method);

        // ========== 1. 放行无需登录的接口（核心修复：加入注册接口） ==========
        // 精准匹配：登录(/auth/login)、注册(/users)，避免模糊匹配
        if (isSkipUrl(uri,method)) {
            log.info("检查到无需登录的请求（登录/注册），直接放行");
            BaseContext.setCurrentUsername("login");
            BaseContext.setCurrentId(0L);
            chain.doFilter(request, response);
            log.info("无需登录的请求已放行");
            return;
        }

        // ========== 2. 校验token ==========
        String token = req.getHeader("token");
        // 2.1 token为空 → 返回401（规范响应流）
        if (token == null || token.isEmpty()) {
            log.info("检查到token为空，返回401");
            returnJson(res, 401, "未登录");
            return;
        }

        // 2.2 解析token → 异常则返回401
        Claims claims;
        try {
            claims = JwtUtil.solveToken(token);
        } catch (Exception e) {
            log.error("解析token失败", e);
            returnJson(res, 401, "登录已过期");
            return;
        }

        // ========== 3. 解析token中的用户信息（处理空指针） ==========
        log.info("token解析成功，提取用户信息");
        Integer id = claims.get("id", Integer.class);
        String username = claims.get("username", String.class);

        // 空指针防护：id/username为空则返回401
        if (id == null || username == null) {
            log.info("token中用户信息缺失");
            returnJson(res, 401, "登录信息异常");
            return;
        }

        // 存入线程上下文
        BaseContext.setCurrentId(Long.valueOf(id));
        BaseContext.setCurrentUsername(username);
        log.info("用户信息已存入线程上下文，放行请求");

        // ========== 4. 放行请求 ==========
        chain.doFilter(request, response);
        log.info("请求已放行");
    }

    /**
     * 封装JSON响应（规范流处理，避免响应丢失）
     * @param res 响应对象
     * @param code 状态码
     * @param message 提示信息
     */
    private void returnJson(HttpServletResponse res, int code, String message) throws IOException {
        res.setStatus(code);
        res.setContentType("application/json;charset=utf-8");
        // 统一响应格式，和你的Result类对齐
        String json = String.format("{\"code\":%d,\"message\":\"%s\",\"data\":null}", code, message);

        PrintWriter writer = res.getWriter();
        writer.write(json);
        writer.flush(); // 强制刷新流，确保响应发送
        writer.close(); // 关闭流，避免资源泄漏
    }

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig config) throws ServletException {
//        log.info("初始化登录过滤器");
    }

    //辅助方法 判断是否是已经声明可以跳过的请求
    private boolean isSkipUrl(String uri,String method) {
        for(int i = 0 ; i < SKIP_URIS.size(); i++)
        {
            if(!uri.equals(SKIP_URIS.get(i)))
            {//不匹配 下一条校验
                continue;
            }
            //通过uri校验 接着校验方法对不对(POST之类)
            String currentMethod = SKIP_URIS_METHODS.get(i);
            if(currentMethod.isEmpty())
            {//没有做额外限定
                return true;
            }
            else
            {//有额外的限定 比如注册是POST
//                log.warn("你好 拦你");
                if(method.equals(currentMethod))
                {
//                    log.warn("你过关!");
                    return true;
                }
            }

        }
        return false;
    }
}