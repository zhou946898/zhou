package com.zyq.springbootthymeleaf.controller;

import com.zyq.springbootthymeleaf.entity.Article;
import com.zyq.springbootthymeleaf.entity.User;
import com.zyq.springbootthymeleaf.repository.ArticleRepository;
import com.zyq.springbootthymeleaf.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/createPost")
    public String createPost(@RequestParam String postTitle, @RequestParam String postCategory, @RequestParam String postContent, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }

        Article article = new Article();
        article.setTitle(postTitle);
        article.setCategory(postCategory);
        article.setContent(postContent);
        article.setPublishDate(new Date());
        article.setUser(user);

        articleRepository.save(article);

        model.addAttribute("user", user); // 添加 user 对象到模型
        model.addAttribute("message", "文章发布成功！");
        return "dashboard";
    }

    // 新增获取文章列表的接口
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @GetMapping("/articles")
    @ResponseBody
    public List<Article> getArticles() {
        List<Article> articles = articleRepository.findAll();
        articles.forEach(article -> {
            logger.info("文章标题: {}, 文章内容: {}", article.getTitle(), article.getContent());
            // 这里假设获取文章内容的方法是getContent，根据实际实体类方法调整
        });
        return articles;
    }

    @GetMapping("/article/{id}")
    @ResponseBody
    public Article getArticleById(@PathVariable Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    // 新增方法，获取当前用户发布的文章并添加到模型
    @GetMapping("/aboutMe")
    public String getAboutMePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        List<Article> userArticles = articleRepository.findTop3ByUserUsernameOrderByPublishDateDesc(username);
        model.addAttribute("user", user);
        model.addAttribute("userArticles", userArticles);
        return "dashboard"; // 假设你的关于我页面是 dashboard.html，根据实际情况修改
    }

    @GetMapping("/user/articles")
    @ResponseBody
    public List<Article> getUserArticles(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return List.of();
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return List.of();
        }
        return articleRepository.findArticlesByUserId(user.getId());
    }

    // 删除文章的方法
    @DeleteMapping("/article/delete/{id}")
    @ResponseBody
    public String deleteArticle(@PathVariable Long id, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "未登录，无法删除文章";
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "用户不存在，无法删除文章";
        }
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return "文章不存在，无法删除";
        }
        if (!article.getUser().getUsername().equals(username)) {
            return "你没有权限删除这篇文章";
        }
        articleRepository.deleteById(id);
        return "文章删除成功";
    }

    // 修改文章的方法
    @PostMapping("/article/update/{id}")
    @ResponseBody
    public String updateArticle(@PathVariable Long id, @RequestParam String postTitle, @RequestParam String postContent, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "未登录，无法修改文章";
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "用户不存在，无法修改文章";
        }
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return "文章不存在，无法修改";
        }
        if (!article.getUser().getUsername().equals(username)) {
            return "你没有权限修改这篇文章";
        }
        article.setTitle(postTitle);
        article.setContent(postContent);
        article.setPublishDate(new Date());
        articleRepository.save(article);
        return "文章修改成功";
    }
}