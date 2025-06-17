// src/main/java/com/example/demo/entity/PortfolioProject.java
package com.zyq.springbootthymeleaf.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "portfolio_project")
public class PortfolioProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // 项目标题
    private String description;    // 项目描述
    private String content;        // 项目详情内容
    private String category;       // 项目分类（website/app/data）
    private String coverImage;     // 封面图片路径
    private String projectUrl;     // 项目访问URL
    private String githubUrl;      // GitHub仓库URL
    private String duration;       // 项目周期

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;        // 开始日期

    @ElementCollection
    private List<String> tags;     // 项目标签
}