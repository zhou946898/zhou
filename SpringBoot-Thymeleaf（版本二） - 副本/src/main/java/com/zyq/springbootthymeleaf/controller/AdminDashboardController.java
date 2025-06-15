package com.zyq.springbootthymeleaf.controller;

import com.zyq.springbootthymeleaf.entity.Article;
import com.zyq.springbootthymeleaf.entity.User;
import com.zyq.springbootthymeleaf.repository.ArticleRepository;
import com.zyq.springbootthymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;

    // 加载用户管理内容（含搜索）
    @GetMapping("/user")
    public String userDashboard(
            @RequestParam(value = "username", required = false) String username,
            Model model) {
        List<User> users = username != null
                ? userRepository.findByUsernameContaining(username)
                : userRepository.findAll();
        model.addAttribute("users", users);
        return "fragments/user_table :: userTableFragment"; // Thymeleaf 片段
    }

    // 加载文章管理内容（含搜索）
    @GetMapping("/article")
    public String articleDashboard(
            @RequestParam(value = "title", required = false) String title,
            Model model) {
        List<Article> articles = title != null
                ? articleRepository.findByTitleContaining(title)
                : articleRepository.findAll();
        model.addAttribute("articles", articles);
        return "fragments/article_table :: articleTableFragment"; // Thymeleaf 片段
    }
}