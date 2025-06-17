// src/main/java/com/example/demo/controller/FileUploadController.java
package com.zyq.springbootthymeleaf.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/portfolio")
    public String uploadPortfolioImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        // 创建保存目录
        File uploadDir = new File(uploadPath + "/portfolio");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // 保存文件
            File dest = new File(uploadDir.getAbsolutePath() + "/" + fileName);
            file.transferTo(dest);
            return "/uploads/portfolio/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败：" + e.getMessage();
        }
    }
}