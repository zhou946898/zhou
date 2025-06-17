package com.zyq.springbootthymeleaf.service.impl;

import com.zyq.springbootthymeleaf.entity.PortfolioProject;
import com.zyq.springbootthymeleaf.repository.PortfolioProjectRepository;
import com.zyq.springbootthymeleaf.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private PortfolioProjectRepository projectRepository;

    // 文件上传目录（根据您的WebConfig配置调整）
    private static final String UPLOAD_DIR = "E:/桌面/javaEE/仿注册/备份/SpringBoot-Thymeleaf（版本二） - 副本/uploads/portfolio/";

    @Override
    public List<PortfolioProject> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public List<PortfolioProject> getProjectsByCategory(String category) {
        return projectRepository.findByCategory(category);
    }

    @Override
    public PortfolioProject getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public PortfolioProject saveProject(PortfolioProject project, MultipartFile file) throws IOException {
        // 处理文件上传
        if (file != null && !file.isEmpty()) {
            // 确保上传目录存在
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 设置项目封面图片路径
            project.setCoverImage(fileName);
        }

        // 保存项目到数据库
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        // 先获取项目信息，用于删除文件
        PortfolioProject project = projectRepository.findById(id).orElse(null);
        if (project != null && project.getCoverImage() != null) {
            try {
                // 删除文件
                Path filePath = Paths.get(UPLOAD_DIR + project.getCoverImage());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 删除数据库记录
        projectRepository.deleteById(id);
    }
}