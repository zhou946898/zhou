package com.zyq.springbootthymeleaf.controller;

import com.zyq.springbootthymeleaf.entity.Admin;
import com.zyq.springbootthymeleaf.entity.Article;
import com.zyq.springbootthymeleaf.entity.User;
import com.zyq.springbootthymeleaf.repository.AdminRepository;
import com.zyq.springbootthymeleaf.repository.ArticleRepository;
import com.zyq.springbootthymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    // 管理员登录页面
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin_login";
    }

    // 处理管理员登录请求
    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            model.addAttribute("error", "管理员用户不存在，请检查。");
            return "admin_login";
        }
        if (!admin.getPassword().equals(password)) {
            model.addAttribute("error", "密码错误，请重新输入。");
            return "admin_login";
        }
        session.setAttribute("adminUsername", username);
        return "redirect:/admin/dashboard";
    }

    // 管理员仪表盘页面
    @GetMapping("/admin/dashboard")
    public String adminDashboard(@RequestParam(value = "username", required = false) String username, Model model, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        List<User> users;
        if (username != null && !username.isEmpty()) {
            users = userRepository.findByUsernameContaining(username);
        } else {
            users = userRepository.findAll();
        }
        model.addAttribute("users", users);
        return "admin_dashboard";
    }

    // 删除用户
    @GetMapping("/admin/user/delete")
    public String deleteUser(@RequestParam Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    // 用户编辑页面
    @GetMapping("/admin/user/edit/{id}")
    public String editUserPage(@PathVariable Long id, Model model, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
        return "admin_user_edit";
    }

    // 处理用户编辑请求
    @PostMapping("/admin/user/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute User user, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(user.getPassword());
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setEducation(user.getEducation());
            existingUser.setHobbies(user.getHobbies());
            userRepository.save(existingUser);
        }
        return "redirect:/admin/dashboard";
    }

    // 管理员退出登录
    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("adminUsername");
        return "redirect:/admin/login";
    }

    // 跳转到修改管理员密码页面
    @GetMapping("/admin/changePasswordPage")
    public String changePasswordPage(Model model, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        return "admin_change_password";
    }

    // 处理修改管理员密码请求
    @PostMapping("/admin/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Model model, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        Admin admin = adminRepository.findByUsername(adminUsername);
        if (!admin.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "旧密码错误，请重新输入。");
            return "admin_change_password";
        }
        admin.setPassword(newPassword);
        adminRepository.save(admin);
        model.addAttribute("message", "密码修改成功，请重新登录。");
        session.removeAttribute("adminUsername");
        return "admin_login";
    }

    // 将用户设置为管理员
    @GetMapping("/admin/setUserAsAdmin/{id}")
    public String setUserAsAdmin(@PathVariable Long id, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            Admin newAdmin = new Admin();
            newAdmin.setUsername(user.getUsername());
            newAdmin.setPassword(user.getPassword());
            adminRepository.save(newAdmin);
            // 移除删除用户的代码
            // userRepository.deleteById(id);
        }
        return "redirect:/admin/dashboard";
    }

    // 管理员文章管理页面
    @GetMapping("/admin/article/dashboard")
    public String adminArticleDashboard(@RequestParam(value = "title", required = false) String title, Model model, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        List<Article> articles;
        if (title != null && !title.isEmpty()) {
            articles = articleRepository.findByTitleContaining(title);
        } else {
            articles = articleRepository.findAll();
        }
        model.addAttribute("articles", articles);
        return "admin_article_dashboard";
    }

    // 删除文章
    @GetMapping("/admin/article/delete")
    public String deleteArticle(@RequestParam Long id) {
        articleRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    // 文章编辑页面
    @GetMapping("/admin/article/edit/{id}")
    public String editArticlePage(@PathVariable Long id, Model model, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        Article article = articleRepository.findById(id).orElse(null);
        model.addAttribute("article", article);
        return "admin_article_edit";
    }

    // 处理文章编辑请求
    @PostMapping("/admin/article/edit/{id}")
    public String editArticle(@PathVariable Long id, @ModelAttribute Article article, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }
        Article existingArticle = articleRepository.findById(id).orElse(null);
        if (existingArticle != null) {
            existingArticle.setTitle(article.getTitle());
            existingArticle.setCategory(article.getCategory());
            existingArticle.setContent(article.getContent());
            articleRepository.save(existingArticle);
        }
        return "redirect:/admin/dashboard";
    }
}