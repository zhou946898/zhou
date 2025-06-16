package com.zyq.springbootthymeleaf.controller;

import com.zyq.springbootthymeleaf.entity.User;
import com.zyq.springbootthymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class VerificationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final Map<String, String> verificationCodeMap = new HashMap<>();
    private static final Map<String, Long> verificationCodeExpirationMap = new HashMap<>();


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // 发送邮箱验证码
    @GetMapping("/sendVerificationCode")
    @ResponseBody
    public void sendVerificationCode(@RequestParam String email) {
        String verificationCode = generateRandomCode(6);
        verificationCodeMap.put(email, verificationCode);
        // 记录验证码的过期时间，有效期 5 分钟
        verificationCodeExpirationMap.put(email, System.currentTimeMillis() + 5 * 60 * 1000);

        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2229339794@qq.com");
        message.setTo(email);
        message.setSubject("注册验证码");
        message.setText("【测试注册】您的注册验证码是：" + verificationCode+"请在五分钟内输入完成验证");
        mailSender.send(message);

        System.out.println("发送验证码到邮箱: " + email + "，验证码: " + verificationCode);
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam String name, @RequestParam String email,
                           @RequestParam String verificationCode,
                           @RequestParam String education, @RequestParam String hobbies,
                           Model model) {
        // 验证验证码
        String correctCode = verificationCodeMap.get(email);
        Long expirationTime = verificationCodeExpirationMap.get(email);
        if (correctCode == null || expirationTime == null ||!correctCode.equals(verificationCode) || System.currentTimeMillis() > expirationTime) {
            return "验证码有误或已过期，请重新获取。";
        }
        // 移除已验证的验证码信息
        verificationCodeMap.remove(email);
        verificationCodeExpirationMap.remove(email);

        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            return "用户名已存在，请选择其他用户名。";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);
        user.setEducation(education);
        user.setHobbies(hobbies);
        userRepository.save(user);
        return "注册成功";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "用户不存在，请先注册。");
            return "login";
        }
        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "密码错误，请重新输入。");
            return "login";
        }
        session.setAttribute("username", username);
        model.addAttribute("user", user);
        return "dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            userRepository.save(user);
            session.invalidate();
            model.addAttribute("message", "密码修改成功，请重新登录。");
            return "login";
        } else {
            model.addAttribute("error", "原密码错误，请重新输入。");
            return "dashboard";
        }
    }

    // 生成随机验证码
    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}