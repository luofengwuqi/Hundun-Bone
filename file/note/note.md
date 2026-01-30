# 问题
在这里汇总一些频繁出现的问题
## lombok在使用之初失效
根源:IDEA没有正确识别注解处理器
### 解决方法一:
IDEA右边侧边栏>maven>生命周期>clean>compile  
搞好后字节码文件(.class)会有get和set 但还有可能出现没有彻底解决的情况
### 解决方法二:
```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <!--这一步是重点-->
        <version>${lombok.version}</version>
        <!--这一步是重点-->
    </path>
</annotationProcessorPaths>
```
这样搞完之后应该就没事了
## 跨域资源共享（CORS）策略拦截导致请求失败
浏览器的 “同源策略” 限制了不同源（协议 / 域名 / 端口任意一个不同）的请求，你的前端（本地 HTML 文件，协议是file://）与后端（http://localhost:8080）属于不同源，因此浏览器拦截了请求（核心是后端响应未携带Access-Control-Allow-Origin头）。  
### 后端（Spring Boot）解决方法:添加以下的配置类
```java
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有后端接口跨域
                .allowedOrigins("*") // 允许所有前端源（开发环境用，生产环境需指定具体域名）
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的请求方法
                .allowedHeaders("*") // 允许的请求头
                .maxAge(3600); // 预检请求的缓存时间（秒）
    }
}
```

