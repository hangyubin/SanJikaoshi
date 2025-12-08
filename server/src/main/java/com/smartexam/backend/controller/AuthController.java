package com.smartexam.backend.controller;

import com.smartexam.backend.entity.User;
import com.smartexam.backend.repository.UserRepository;
import com.smartexam.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    // 登录请求
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        // 获取用户名和密码
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // 获取认证后的用户信息
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 生成JWT令牌
            String token = jwtUtils.generateToken(userDetails.getUsername());
            
            // 获取完整的用户信息
            Optional<User> optionalUser = userRepository.findByUsername(username);
            User user = optionalUser.get();
            
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("token", token);
            response.put("refreshToken", jwtUtils.refreshToken(token));
            response.put("username", user.getUsername());
            response.put("realName", user.getRealName());
            response.put("role", user.getRole());
            
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            response.put("code", 400);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.ok(response);
        }
    }
    
    // 注册请求
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User registerUser) {
        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(registerUser.getUsername());
        if (existingUser.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "用户名已存在");
            return ResponseEntity.ok(response);
        }
        
        // 检查邮箱是否已存在
        Optional<User> existingEmail = userRepository.findByEmail(registerUser.getEmail());
        if (existingEmail.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "邮箱已存在");
            return ResponseEntity.ok(response);
        }
        
        // 加密密码
        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        
        // 设置默认值
        registerUser.setStatus(1); // 启用状态
        registerUser.setCreateTime(new Date().getTime());
        registerUser.setUpdateTime(new Date().getTime());
        
        // 保存用户
        User savedUser = userRepository.save(registerUser);
        
        // 生成JWT令牌
        String token = jwtUtils.generateToken(savedUser.getUsername());
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "注册成功");
        response.put("token", token);
        response.put("refreshToken", jwtUtils.refreshToken(token));
        response.put("user", savedUser);
        
        return ResponseEntity.ok(response);
    }
    
    // 刷新令牌请求
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshRequest) {
        // 获取旧令牌
        String oldToken = refreshRequest.get("token");
        
        Map<String, Object> response = new HashMap<>();
        
        if (oldToken == null) {
            response.put("code", 400);
            response.put("message", "令牌不能为空");
            return ResponseEntity.ok(response);
        }
        
        // 刷新令牌
        String newToken = jwtUtils.refreshToken(oldToken);
        
        if (newToken == null) {
            response.put("code", 400);
            response.put("message", "令牌已过期或无效");
            return ResponseEntity.ok(response);
        }
        
        response.put("code", 200);
        response.put("message", "令牌刷新成功");
        response.put("token", newToken);
        response.put("refreshToken", jwtUtils.refreshToken(newToken));
        
        return ResponseEntity.ok(response);
    }
}