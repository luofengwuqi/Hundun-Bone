package com.createpen.fifteenhundun.module.article.mapper;

import com.createpen.fifteenhundun.module.article.pojo.Entity.ArticleUserInteract;
import com.createpen.fifteenhundun.module.article.pojo.DTO.ArticleUpdateDTO;
import com.createpen.fifteenhundun.module.article.pojo.Entity.Article;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Select("select * from article")
    List<Article> selectArticleList();

    @Insert("INSERT INTO article (title, text, creator, belonging , created_at) VALUES (#{title}, #{text}, #{creator}, #{belonging},#{createdAt})")
    void insertArticle(Article article);

    @Select("select * from article where id = #{id}")
    Article selectArticleById(Integer id);

    void batchUpdateArticleGoodCount(@Param("list") List<ArticleUpdateDTO> list);

    void batchUpdateArticleBadCount(List<ArticleUpdateDTO> articleUpdateDTOS);

    void batchInsertArticleUserInteract(List<ArticleUserInteract> goodUserRelationMap);
}
