package com.example.tokenlogin.mapper;

import com.example.tokenlogin.model.Article;
import com.example.tokenlogin.model.Paging;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    void saveArticle(Article article);
    List<Article> getArticles(Paging page);
    int getArticleCnt();
    Article getArticleById(long id);
    void updateArticle(Article article);
    void deleteArticle(long id);

}
