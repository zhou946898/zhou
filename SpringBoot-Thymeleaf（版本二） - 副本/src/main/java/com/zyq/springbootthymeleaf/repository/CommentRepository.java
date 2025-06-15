package com.zyq.springbootthymeleaf.repository;

import com.zyq.springbootthymeleaf.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleIdOrderByDateDesc(Long articleId);
}