package com.smartexam.backend.repository;

import com.smartexam.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根据用户名查询用户
    Optional<User> findByUsername(String username);
    
    // 根据用户名和真实姓名模糊查询
    List<User> findByUsernameContainingOrRealNameContaining(String username, String realName);
    
    // 根据用户名和真实姓名模糊查询，并带状态过滤
    List<User> findByUsernameContainingOrRealNameContainingAndStatus(String username, String realName, Integer status);
    
}


