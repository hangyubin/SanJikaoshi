package com.smartexam.backend.controller;

import com.smartexam.backend.entity.User;
import com.smartexam.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 获取所有用户
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = userRepository.findAll();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", optionalUser.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据用户名获取用户
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isPresent()) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", optionalUser.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 创建用户
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 检查用户名是否已存在
            Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
            if (existingUser.isPresent()) {
                response.put("code", 400);
                response.put("message", "用户名已存在");
                return ResponseEntity.ok(response);
            }

            User savedUser = userRepository.save(user);
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                
                // 更新用户信息
                existingUser.setRealName(user.getRealName());
                existingUser.setPhone(user.getPhone());
                existingUser.setDepartment(user.getDepartment());
                existingUser.setJobTitle(user.getJobTitle());
                existingUser.setRoles(user.getRoles());
                existingUser.setStatus(user.getStatus());
                existingUser.setUpdateTime(System.currentTimeMillis());
                
                User updatedUser = userRepository.save(existingUser);
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updatedUser);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                userRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量删除用户
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteUsers(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            userRepository.deleteAllById(ids);
            response.put("code", 200);
            response.put("message", "批量删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}