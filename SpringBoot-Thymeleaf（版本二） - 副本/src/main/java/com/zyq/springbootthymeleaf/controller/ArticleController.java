package com.zyq.springbootthymeleaf.controller;

import com.zyq.springbootthymeleaf.entity.Article;
import com.zyq.springbootthymeleaf.entity.Comment;
import com.zyq.springbootthymeleaf.entity.User;
import com.zyq.springbootthymeleaf.repository.ArticleRepository;
import com.zyq.springbootthymeleaf.repository.CommentRepository;
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
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    // 文章详情页面
    @GetMapping("/article/details/{id}")
    public String articleDetails(@PathVariable Long id, Model model, HttpSession session) {
        // 获取当前登录用户
        String username = (String) session.getAttribute("username");
        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username);
            model.addAttribute("user", user);
        }

        // 获取文章信息
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            model.addAttribute("errorMessage", "文章不存在");
            return "error";
        }

        // 增加文章阅读量
        article.setViews(article.getViews() == null ? 1 : article.getViews() + 1);
        articleRepository.save(article);

        // 获取文章评论
        List<Comment> comments = commentRepository.findByArticleIdOrderByDateDesc(id);
        model.addAttribute("comments", comments);
        model.addAttribute("article", article);
        return "article_details";
    }

    // 添加评论
    @PostMapping("/article/{articleId}/comment")
    @ResponseBody
    public String addComment(@PathVariable Long articleId, @RequestParam String content, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "未登录，无法评论";
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "用户不存在，无法评论";
        }

        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) {
            return "文章不存在，无法评论";
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(username);
        comment.setDate(new Date());
        comment.setArticle(article);

        commentRepository.save(comment);

        return "评论成功";
    }

    // 获取文章评论
    @GetMapping("/articles/{articleId}/comments")
    @ResponseBody
    public List<Comment> getArticleComments(@PathVariable Long articleId) {
        return commentRepository.findByArticleIdOrderByDateDesc(articleId);
    }

    // 其他已有的方法保持不变...
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

        model.addAttribute("user", user);
        model.addAttribute("message", "文章发布成功！");
        return "dashboard";
    }

    @GetMapping("/articles")
    @ResponseBody
    public List<Article> getArticles() {
        List<Article> articles = articleRepository.findAll();
        articles.forEach(article -> {
            logger.info("文章标题: {}, 文章内容: {}", article.getTitle(), article.getContent());
        });
        return articles;
    }



    @GetMapping("/article/{id}")
    @ResponseBody
    public Article getArticleById(@PathVariable Long id) {
        return articleRepository.findById(id).orElse(null);
    }

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
        return "dashboard";
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

    // 删除评论
    @DeleteMapping("/article/{articleId}/comment/{commentId}")
    @ResponseBody
    public String deleteComment(@PathVariable Long articleId, @PathVariable Long commentId, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "未登录，无法删除评论";
        }

        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return "评论不存在，无法删除";
        }

        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) {
            return "文章不存在，无法删除评论";
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "用户不存在，无法删除评论";
        }

        // 检查是否是评论作者或文章作者
        if (!comment.getAuthor().equals(username) && !article.getUser().getUsername().equals(username)) {
            return "你没有权限删除这条评论";
        }

        commentRepository.deleteById(commentId);
        return "评论删除成功";
    }

    // 点赞评论
    @PostMapping("/article/{articleId}/comment/{commentId}/like")
    @ResponseBody
    public String likeComment(@PathVariable Long articleId, @PathVariable Long commentId, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "未登录，无法点赞评论";
        }

        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return "评论不存在，无法点赞";
        }

        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) {
            return "文章不存在，无法点赞评论";
        }

        // 模拟点赞逻辑，这里简单增加点赞数
        comment.setLikes(comment.getLikes() == null ? 1 : comment.getLikes() + 1);
        commentRepository.save(comment);
        return "评论点赞成功";
    }

}
