package com.createpen.fifteenhundun.module.article.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.createpen.fifteenhundun.common.constant.redis.ArticleRedisConstant.*;

@Slf4j
@Component
//这个类旨在从redis拿到一天的点赞/点踩数据
//包括:
// 1点赞数量/点踩数量
// 2 点赞关联用户/点踩关联用户
public class ArticleRedisToMysqlService {
    private final StringRedisTemplate stringRedisTemplate;

    public ArticleRedisToMysqlService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    //1.1 点赞数量
    //返回值说明: <articleId,点赞数>
    public Map<Integer,Integer> selectAllArticleGoodCount(){
         return selectAllArticleCount(ARTICLE_GOOD_COUNT);
    }
    //1.2 点踩数量

    public Map<Integer,Integer> selectAllArticleBadCount() {
        return selectAllArticleCount(ARTICLE_BAD_COUNT);
    }
    //抽取方法
    public Map<Integer,Integer> selectAllArticleCount(String redisKey){
        log.info("开始获取所有文章点赞数量List集合");
        
        // 使用 try-with-resources 确保 Cursor 被正确关闭
        try (Cursor<String> scan = stringRedisTemplate.scan(
                ScanOptions.scanOptions()
                        .match(redisKey + "*")
                        .count(100)
                        .build())) {
            
            Map<Integer, Integer> map = new HashMap<>();
            
            // 边扫描边处理，避免存储两份数据
            while (scan.hasNext()) {
                String key = scan.next();
                
                // 从 key 中提取 articleId
                // key 格式: article:good:count:123
                String articleIdStr = key.substring(redisKey.length());
                
                try {
                    Integer articleId = Integer.parseInt(articleIdStr);
                    
                    // 获取对应的 value
                    String valueStr = stringRedisTemplate.opsForValue().get(key);
                    Integer value = valueStr == null ? 0 : Integer.parseInt(valueStr);
                    
                    map.put(articleId, value);
                    
                } catch (NumberFormatException e) {
                    log.warn("无法解析文章ID: {}", articleIdStr);
                }
            }
            
            return map;  // 返回空 Map 而不是 null
            
        } catch (Exception e) {
            log.error("获取所有文章点赞/点踩数量List集合失败", e);
            throw new RuntimeException("获取所有文章点赞/点踩数量List集合失败", e);
        }
    }

    //1.3 移除数量记录
    public void deleteAllArticleCount() {
        log.info("开始移除所有文章点赞/点踩数量记录");
        
        // 删除点赞数量记录
        deleteKeysByPattern(ARTICLE_GOOD_COUNT + "*");
        
        // 删除点踩数量记录
        deleteKeysByPattern(ARTICLE_BAD_COUNT + "*");
        
        log.info("完成移除所有文章点赞/点踩数量记录");
    }
    
    /**
     * 根据模式删除Redis keys
     * @param pattern key匹配模式
     */
    private void deleteKeysByPattern(String pattern) {
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();
        
        try (Cursor<String> cursor = stringRedisTemplate.scan(scanOptions)) {
            java.util.List<String> keysToDelete = new java.util.ArrayList<>();
            
            while (cursor.hasNext()) {
                keysToDelete.add(cursor.next());
                
                // 批量删除，每50个key删除一次
                if (keysToDelete.size() >= 50) {
                    stringRedisTemplate.delete(keysToDelete);
                    keysToDelete.clear();
                }
            }
            
            // 删除剩余的key
            if (!keysToDelete.isEmpty()) {
                stringRedisTemplate.delete(keysToDelete);
            }
        } catch (Exception e) {
            log.error("删除匹配 {} 的记录时发生错误", pattern, e);
        }
    }

    //2.1 点赞关联用户
    //返回值说明: Map<articleId, List<userId>>
    public Map<Long, List<Long>> selectAllArticleGoodUserRelations() {
        return selectAllArticleUserRelations(ARTICLE_GOOD_USER);
    }
    //2.2 点踩关联用户
    public Map<Long, List<Long>> selectAllArticleBadUserRelations() {
        return selectAllArticleUserRelations(ARTICLE_BAD_USER);
    }
    //抽取方法
    public Map<Long, List<Long>> selectAllArticleUserRelations(String redisKey) {
        log.info("开始获取所有文章点赞用户关系");
        
        // 使用 try-with-resources 确保 Cursor 被正确关闭
        try (Cursor<String> scan = stringRedisTemplate.scan(
                ScanOptions.scanOptions()
                        .match(redisKey + "*")
                        .count(100)
                        .build())) {
            
            Map<Long, List<Long>> relationMap = new HashMap<>();
            
            // 边扫描边处理，避免存储两份数据
            while (scan.hasNext()) {
                String key = scan.next();
                
                // 从 key 中提取 articleId 和 userId
                // key 格式: article:good:user:{articleId}:{userId}
                try {
                    String remaining = key.substring(redisKey.length());
                    String[] parts = remaining.split(":");
                    
                    if (parts.length == 2) {
                        Long articleId = Long.parseLong(parts[0]);
                        Long userId = Long.parseLong(parts[1]);
                        
                        // 将 userId 添加到对应 articleId 的列表中
                        relationMap.computeIfAbsent(articleId, k -> new ArrayList<>())
                                   .add(userId);
                    } else {
                        log.warn("Key 格式不正确: {}", key);
                    }
                    
                } catch (NumberFormatException e) {
                    log.warn("无法解析文章ID或用户ID, key: {}", key);
                }
            }
            
            log.info("获取到 {} 篇文章的点赞用户关系", relationMap.size());
            return relationMap;
            
        } catch (Exception e) {
            log.error("获取所有文章点赞用户关系失败", e);
            throw new RuntimeException("获取所有文章点赞用户关系失败", e);
        }
    }

}
