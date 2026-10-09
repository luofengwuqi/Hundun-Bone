package com.createpen.fifteenhundun.framework.interception;

import com.createpen.fifteenhundun.common.constant.status.UserStatusConstant;
import com.createpen.fifteenhundun.common.context.BaseContext;
import com.createpen.fifteenhundun.common.util.JwtUtil;
import com.createpen.fifteenhundun.module.user.pojo.Entity.User;
import com.createpen.fifteenhundun.module.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static com.createpen.fifteenhundun.common.constant.redis.RedisConstant.TOKEN_PATH;

/**
 * 登录校验过滤器（修复核心问题：放行注册接口、规范响应、处理空指针、避免重复注册）
 */
@Slf4j
@Component // 仅用Spring管理，去掉@WebFilter避免重复注册
public class LoginFilter implements Filter {

    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    //预先声明直接放行的请求
    private static final List<String> SKIP_URIS = Arrays.asList(
            "/user/login",                        // /user/login 下的所有接口
            "/user/register"                              // 注册接口
    );
    //对应 SKIP_URIS 中需要限制方法的路径（索引对应
    private static final List<String> SKIP_URIS_METHODS = Arrays.asList(
            "",                   // /user/login/*：允许所有方法
            "POST"               // 注册接口：仅允许 POST
    );

    public LoginFilter(RedisTemplate<String, Object> redisTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

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
        if (method.equals("OPTIONS")){
            log.info("OPTIONS请求，拦这个干啥 放了");
            chain.doFilter(request, response);
            return;
        }
        if (isSkipUrl(uri,method) && !uri.equals("/user/login/logout")) {
            log.info("检查到无需登录的请求（登录/注册），直接放行");
            BaseContext.setCurrentUsername("login");
            BaseContext.setCurrentId(0L);
            chain.doFilter(request, response);
            log.info("无需登录的请求已放行");
            return;
        }

        // ========== 2. 校验 token ==========
        // 优先从 Authorization 头获取（标准格式：Bearer <token>）
        String authorization = req.getHeader("Authorization");
        String token;
                
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7); // 去掉 "Bearer " 前缀
        } else {
            // 兼容旧的 token 头方式
            token = req.getHeader("token");
        }

        // 2.1 token 为空 → 返回 401（规范响应流）
        if (token == null || token.isEmpty()) {
            log.info("检查到 token 为空，返回 401");
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
        //2.3 检验token在redis是否过期
        Integer id = claims.get("id", Integer.class);
        String redisToken;
        try{
            redisToken  = (String) redisTemplate.opsForValue().get(TOKEN_PATH + id);
        }catch (Exception e){
            log.info("从redis获取token失败，返回401");
            returnJson(res, 401, "登录已过期...");
            return;
        }
        if (redisToken == null || !redisToken.equals(token)) {
            log.info("检查到token已过期，返回401");
            returnJson(res, 401, "登录已过期...");
            return;
        }

        // ========== 3. 解析token中的用户信息（处理空指针+防止普通用户越级访问） ==========
        log.info("token解析成功，提取用户信息");
        String username = claims.get("username", String.class);

        // 空指针防护：id/username为空则返回401
        if (id == null || username == null) {
            log.info("token中用户信息缺失");
            returnJson(res, 401, "登录信息异常");
            return;
        }
        //防止普通用户越级访问
        if (uri.startsWith("/admin")) {
            User user = userService.selectUserById(Long.valueOf(id));
            Integer status = user.getStatus();
            if(status < UserStatusConstant.STATUS_ADMIN){
                log.warn("{}发生越级访问!!!",username);
                returnJson(res, 401, "越级访问!!!");
                return;
            }

        }



        // 存入线程上下文
        BaseContext.setCurrentId(Long.valueOf(id));
        BaseContext.setCurrentUsername(username);
        log.info("用户信息已存入线程上下文，放行请求");

        // ========== 4. 放行请求 ==========
        //更新redis中token过期时间
        redisTemplate.expire(TOKEN_PATH + id, 30, java.util.concurrent.TimeUnit.MINUTES);
        chain.doFilter(request, response);
        log.info("通过Filter验证 请求已放行");
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
    public void init(FilterConfig config)  {
//        log.info("初始化登录过滤器");
    }

    //辅助方法 判断是否是已经声明可以跳过的请求
    private boolean isSkipUrl(String uri,String method) {
        //管理员注册
        if(uri.equals("/admin") && method.equals("POST")){
            log.info("管理员注册 放行");
            return true;
        }
        for(int i = 0 ; i < SKIP_URIS.size(); i++)
        {
            String skipUri = SKIP_URIS.get(i);
            // 判断是否为子路径匹配（如 /user/login/email-password 匹配 /user/login）
            boolean isMatch = uri.equals(skipUri) || uri.startsWith(skipUri + "/");
            
            if(!isMatch)
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
                if(method.equals(currentMethod))
                {
                    return true;
                }
            }

        }
        return false;
    }
}