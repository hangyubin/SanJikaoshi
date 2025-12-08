package com.smartexam.backend.controller;

import com.smartexam.backend.entity.User;
import com.smartexam.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 登录请求
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        // 获取用户名和密码
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        
        // 检查用户名是否存在
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            response.put("code", 400);
            response.put("message", "用户名不存在");
            return ResponseEntity.ok(response);
        }
        
        User user = optionalUser.get();
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("code", 400);
            response.put("message", "密码错误");
            return ResponseEntity.ok(response);
        }
        
        response.put("code", 200);
        response.put("message", "登录成功");
        response.put("username", user.getUsername());
        response.put("realName", user.getRealName());
        
        return ResponseEntity.ok(response);
    }
    
    // 注册请求
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User registerUser) {
        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(registerUser.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        Optional<User> existingEmail = userRepository.findByEmail(registerUser.getEmail());
        if (existingEmail.isPresent()) {
            return ResponseEntity.badRequest().body("邮箱已存在");
        }
        
        // 加密密码
        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        
        // 设置默认值
        registerUser.setStatus(1); // 启用状态
        registerUser.setCreateTime(new Date().getTime());
        registerUser.setUpdateTime(new Date().getTime());
        
        // 保存用户
        User savedUser = userRepository.save(registerUser);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "注册成功");
        response.put("user", savedUser);
        
        return ResponseEntity.ok(response);
    }
}