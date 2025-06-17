package com.zyq.springbootthymeleaf.controller;

import com.zyq.springbootthymeleaf.entity.PortfolioProject;
import com.zyq.springbootthymeleaf.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    // 获取所有项目
    @GetMapping("/projects")
    public List<PortfolioProject> getAllProjects() {
        return portfolioService.getAllProjects();
    }

    // 根据分类获取项目
    @GetMapping("/projects/category/{category}")
    public List<PortfolioProject> getProjectsByCategory(@PathVariable String category) {
        return portfolioService.getProjectsByCategory(category);
    }

    // 获取单个项目详情
    @GetMapping("/projects/{id}")
    public PortfolioProject getProjectById(@PathVariable Long id) {
        return portfolioService.getProjectById(id);
    }

    // 创建新项目
    @PostMapping("/projects")
    public ResponseEntity<Map<String, Object>> createProject(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam("projectUrl") String projectUrl,
            @RequestParam("githubUrl") String githubUrl,
            @RequestParam("duration") String duration,
            @RequestParam("startDate") String startDate,
            @RequestParam("tags") String tags,
            @RequestParam(value = "coverImage", required = false) MultipartFile file) {

        Map<String, Object> result = new HashMap<>();

        try {
            PortfolioProject project = new PortfolioProject();
            project.setTitle(title);
            project.setDescription(description);
            project.setContent(content);
            project.setCategory(category);
            project.setProjectUrl(projectUrl);
            project.setGithubUrl(githubUrl);
            project.setDuration(duration);
            project.setStartDate(java.sql.Date.valueOf(startDate)); // 转换日期格式
            project.setTags(List.of(tags.split(","))); // 转换标签为列表

            PortfolioProject savedProject = portfolioService.saveProject(project, file);

            result.put("success", true);
            result.put("message", "项目创建成功");
            result.put("data", savedProject);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "项目创建失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // 更新项目
    @PutMapping("/projects/{id}")
    public ResponseEntity<Map<String, Object>> updateProject(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam("projectUrl") String projectUrl,
            @RequestParam("githubUrl") String githubUrl,
            @RequestParam("duration") String duration,
            @RequestParam("startDate") String startDate,
            @RequestParam("tags") String tags,
            @RequestParam(value = "coverImage", required = false) MultipartFile file) {

        Map<String, Object> result = new HashMap<>();

        try {
            PortfolioProject project = portfolioService.getProjectById(id);
            if (project == null) {
                result.put("success", false);
                result.put("message", "项目不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            project.setTitle(title);
            project.setDescription(description);
            project.setContent(content);
            project.setCategory(category);
            project.setProjectUrl(projectUrl);
            project.setGithubUrl(githubUrl);
            project.setDuration(duration);
            project.setStartDate(java.sql.Date.valueOf(startDate));
            project.setTags(List.of(tags.split(",")));

            PortfolioProject updatedProject = portfolioService.saveProject(project, file);

            result.put("success", true);
            result.put("message", "项目更新成功");
            result.put("data", updatedProject);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "项目更新失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // 删除项目
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Map<String, Object>> deleteProject(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        try {
            portfolioService.deleteProject(id);
            result.put("success", true);
            result.put("message", "项目删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "项目删除失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}