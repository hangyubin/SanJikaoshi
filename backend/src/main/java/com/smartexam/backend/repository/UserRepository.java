package com.smartexam.backend.repository;

import com.smartexam.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根据用户名查询用户
    Optional<User> findByUsername(String username);
    
    // 根据邮箱查询用户
    Optional<User> findByEmail(String email);
}