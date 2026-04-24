package com.createpen.fifteenhundun.module.article.mapper;

import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Select("select * from article")
    List<Article> selectArticleList();

    @Insert("INSERT INTO article (title, text, creator, belonging) VALUES (#{title}, #{text}, #{creator}, #{belonging})")
    void insertArticle(Article article);

    @Select("select * from article where id = #{id}")
    Article selectArticleById(Integer id);
}
