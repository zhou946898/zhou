package com.zyq.springbootthymeleaf.repository;

import com.zyq.springbootthymeleaf.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
}