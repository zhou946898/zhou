package com.zyq.springbootthymeleaf.service;

import com.zyq.springbootthymeleaf.entity.PortfolioProject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PortfolioService {
    List<PortfolioProject> getAllProjects();
    List<PortfolioProject> getProjectsByCategory(String category);
    PortfolioProject getProjectById(Long id);
    PortfolioProject saveProject(PortfolioProject project, MultipartFile file) throws IOException;
    void deleteProject(Long id);
}