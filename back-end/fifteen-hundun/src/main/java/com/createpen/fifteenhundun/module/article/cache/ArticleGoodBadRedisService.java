package com.createpen.fifteenhundun.module.article.cache;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.*;
import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.ARTICLE_BAD_COUNT;

@Slf4j
@Component
public class ArticleGoodBadRedisService {
    private final RedisTemplate<String,Object> redisTemplate;
    public ArticleGoodBadRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    //这是点good的lua脚本
    private DefaultRedisScript<Long> goodScript;
    private DefaultRedisScript<Long> badScript;

    //辅助方法:构建lua脚本
    @PostConstruct
    //在 Spring Bean 完成【实例化 + 依赖注入】之后 自动执行一次标注的方法
    public void initLua() {
        goodScript = new DefaultRedisScript<>();
        goodScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/article/good.lua")));
        goodScript.setResultType(Long.class);
        log.info("已初始化good的lua脚本...");
        badScript = new DefaultRedisScript<>();
        badScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/article/bad.lua")));
        badScript.setResultType(Long.class);
    }
    public Long doScript(Long articleId, Long userId,DefaultRedisScript<Long> script) {
        // 构造 KEYS 和 ARGV 参数
        List<String> keys = Arrays.asList(
                ARTICLE_GOOD_USER + articleId + ":" + userId,      // KEYS[1] - 注意这里改成 Set 结构
                ARTICLE_GOOD_COUNT + articleId,                     // KEYS[2]
                ARTICLE_BAD_USER + articleId+ ":" + userId,       // KEYS[3]
                ARTICLE_BAD_COUNT + articleId                       // KEYS[4]
        );
        log.info("执行good的lua脚本");
        return redisTemplate.execute(
                script,
                keys,
                Collections.emptyList()
        );
    }

    //执行good的lua脚本
    public Long doGoodScript(Long articleId, Long currentId) {
        return doScript(articleId, currentId, goodScript);
    }
    //执行bad的lua脚本
    public Long doBadScript(Long articleId, Long currentId) {
        return doScript(articleId, currentId, badScript);
    }

}
