package com.createpen.fifteenhundun.module.article.mapper;

import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Select("select * from article")
    List<Article> selectArticleList();
}
