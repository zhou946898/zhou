package com.zyq.springbootthymeleaf.repository;

import com.zyq.springbootthymeleaf.entity.PortfolioProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, Long> {
    // 根据分类查询项目
    List<PortfolioProject> findByCategory(String category);

    // 添加根据标题模糊查询的方法
    List<PortfolioProject> findByTitleContaining(String title);
}