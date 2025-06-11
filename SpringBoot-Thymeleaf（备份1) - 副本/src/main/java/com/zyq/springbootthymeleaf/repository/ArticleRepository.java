package com.zyq.springbootthymeleaf.repository;

import com.zyq.springbootthymeleaf.entity.Article;
import com.zyq.springbootthymeleaf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    /**
     * 根据用户名查询最新发布的三篇文章，按发布日期降序排列
     * @param username 用户名
     * @return 文章列表
     */
    List<Article> findTop3ByUserUsernameOrderByPublishDateDesc(String username);

    /**
     * 根据用户 ID 查询文章列表
     * @param userId 用户 ID
     * @return 文章列表
     */
    @Query("SELECT a FROM Article a WHERE a.user.id = :userId")
    List<Article> findArticlesByUserId(@Param("userId") Long userId);
}