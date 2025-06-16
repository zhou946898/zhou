package com.zyq.springbootthymeleaf.repository;

import com.zyq.springbootthymeleaf.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByTitleContaining(String title);
    List<Article> findTop3ByUserUsernameOrderByPublishDateDesc(String username);
    List<Article> findArticlesByUserId(Long userId);
}