package com.zyq.springbootthymeleaf.controller;

import com.zyq.springbootthymeleaf.entity.Admin;
import com.zyq.springbootthymeleaf.entity.Article;
import com.zyq.springbootthymeleaf.entity.User;
import com.zyq.springbootthymeleaf.repository.AdminRepository;
import com.zyq.springbootthymeleaf.repository.ArticleRepository;
import com.zyq.springbootthymeleaf.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    // 修改为实际的上传根目录（包含中文和空格，需要特殊处理）
    private static final String UPLOAD_ROOT_DIR = "E:/桌面/javaEE/仿注册/备份/SpringBoot-Thymeleaf（版本二） - 副本/uploads/";

    // 图片和文件的子目录
    private static final String IMAGE_DIR = "images/";
    private static final String FILE_DIR = "files/";

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
    @GetMapping("/admin/toggleAdmin/{id}")
    @Transactional
    public String toggleAdminStatus(@PathVariable Long id, HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");
        if (adminUsername == null) {
            return "redirect:/admin/login";
        }

        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // 检查用户是否已在admin表中
            Admin existingAdmin = adminRepository.findByUsername(user.getUsername());

            if (existingAdmin != null) {
                // 用户已是管理员，取消管理员身份
                adminRepository.delete(existingAdmin);
                user.setIsAdmin(0);
                logger.info("用户 {} 已被取消管理员身份", user.getUsername());
            } else {
                // 用户不是管理员，设置为管理员
                Admin newAdmin = new Admin();
                newAdmin.setUsername(user.getUsername());
                newAdmin.setPassword(user.getPassword());
                adminRepository.save(newAdmin);
                user.setIsAdmin(1);
                logger.info("用户 {} 已被设置为管理员", user.getUsername());
            }

            userRepository.save(user); // 更新用户状态
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


    // 删除文章（改进版）
    @GetMapping("/admin/article/delete")
    @Transactional
    public String deleteArticle(@RequestParam Long id) {
        // 1. 先查询文章信息，获取文件路径
        Article article = articleRepository.findById(id).orElse(null);
        if (article != null) {
            logger.info("准备删除文章ID: {}", id);

            // 2. 删除关联的图片（如果有）
            boolean imageDeleted = deleteImageFile(article.getImagePath());
            logger.info("图片删除结果: {}, 路径: {}", imageDeleted, article.getImagePath());

            // 3. 删除关联的文件（如果有）
            boolean fileDeleted = deleteFile(article.getFilePath());
            logger.info("文件删除结果: {}, 路径: {}", fileDeleted, article.getFilePath());

            // 4. 删除数据库中的文章记录
            articleRepository.delete(article);
            logger.info("文章记录已从数据库删除");
        }
        return "redirect:/admin/dashboard";
    }

    // 删除图片文件
    private boolean deleteImageFile(String filePath) {
        return deleteFileInternal(filePath, IMAGE_DIR);
    }

    // 删除普通文件
    private boolean deleteFile(String filePath) {
        return deleteFileInternal(filePath, FILE_DIR);
    }

    // 内部文件删除方法
    private boolean deleteFileInternal(String filePath, String subDir) {
        if (filePath == null || filePath.isEmpty()) {
            logger.info("文件路径为空，跳过删除");
            return true;
        }

        try {
            // 解码URL编码的路径（处理空格等特殊字符）
            String decodedPath = UriUtils.decode(filePath, StandardCharsets.UTF_8);

            // 拼接完整路径（根目录 + 子目录 + 文件名）
            Path fullPath = Paths.get(UPLOAD_ROOT_DIR, subDir, decodedPath);
            logger.info("尝试删除文件: {}", fullPath);

            // 检查文件是否存在
            if (!Files.exists(fullPath)) {
                logger.warn("文件不存在: {}", fullPath);
                return false;
            }

            // 确保文件可写
            if (!Files.isWritable(fullPath)) {
                logger.error("文件不可写: {}", fullPath);
                // 尝试修改权限
                if (fullPath.toFile().setWritable(true)) {
                    logger.info("已成功修改文件权限: {}", fullPath);
                } else {
                    logger.error("无法修改文件权限: {}", fullPath);
                    return false;
                }
            }

            // 关闭可能打开的文件流（如果有）
            closeOpenResources(fullPath);

            // 删除文件
            Files.delete(fullPath);
            logger.info("文件删除成功: {}", fullPath);
            return true;
        } catch (Exception e) {
            logger.error("删除文件时发生异常: {}", filePath, e);
            return false;
        }
    }

    // 尝试关闭可能打开的文件资源
    private void closeOpenResources(Path filePath) {
        // 这里可以添加特定于你的应用的资源关闭逻辑
        // 例如，如果使用了文件上传库，可能需要调用其清理方法
        // 对于大多数情况，Java会自动管理文件资源，但某些库可能需要显式关闭
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