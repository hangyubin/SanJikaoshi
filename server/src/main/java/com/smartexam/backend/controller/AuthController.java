package com.smartexam.backend.controller;

import com.smartexam.backend.entity.User;
import com.smartexam.backend.repository.UserRepository;
import com.smartexam.backend.utils.JwtUtils;
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
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    // 登录请求
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        // 获取用户名和密码
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 查找用户
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                // 验证密码
                if (passwordEncoder.matches(password, user.getPassword())) {
                    // 生成JWT令牌
                    String token = jwtUtils.generateToken(user.getUsername());
                    response.put("code", 200);
                    response.put("message", "登录成功");
                    response.put("username", user.getUsername());
                    response.put("realName", user.getRealName());
                    response.put("token", token);
                    // 返回用户角色信息
                    response.put("roles", user.getRoles());
                } else {
                    response.put("code", 400);
                    response.put("message", "用户名或密码错误");
                }
            } else {
                response.put("code", 400);
                response.put("message", "用户名或密码错误");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "登录失败：" + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 注册请求
    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> registerData) {
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查用户名是否已存在
            String username = (String) registerData.get("username");
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                response.put("code", 400);
                response.put("message", "用户名已存在");
                return ResponseEntity.ok(response);
            }
            
            // 创建User对象并手动设置属性
            User registerUser = new User();
            registerUser.setUsername((String) registerData.get("username"));
            registerUser.setPassword((String) registerData.get("password"));
            registerUser.setRealName((String) registerData.get("realName"));
            registerUser.setPhone((String) registerData.get("phone"));
            
            // 加密密码
            registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
            
            // 设置默认值
            registerUser.setStatus(1); // 启用状态
            registerUser.setCreateTime(new Date().getTime());
            registerUser.setUpdateTime(new Date().getTime());
            
            // 保存用户到数据库
            User savedUser = userRepository.save(registerUser);
            
            // 构建响应
            response.put("code", 200);
            response.put("message", "注册成功");
            response.put("data", registerData);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "注册失败：" + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 刷新令牌请求
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshRequest) {
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        
        try {
            String oldToken = refreshRequest.get("token");
            if (oldToken != null) {
                // 刷新JWT令牌
                String newToken = jwtUtils.refreshToken(oldToken);
                if (newToken != null) {
                    response.put("code", 200);
                    response.put("message", "令牌刷新成功");
                    response.put("token", newToken);
                } else {
                    response.put("code", 400);
                    response.put("message", "无效的令牌");
                }
            } else {
                response.put("code", 400);
                response.put("message", "令牌不能为空");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "令牌刷新失败：" + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    

}


