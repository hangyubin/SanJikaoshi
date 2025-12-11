package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Role;
import com.smartexam.backend.entity.User;
import com.smartexam.backend.repository.RoleRepository;
import com.smartexam.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    // 获取所有用户（支持分页、搜索和筛选）
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> allUsers;
            
            // 根据条件查询
            if (status != null) {
                allUsers = userRepository.findByUsernameContainingOrRealNameContainingAndStatus(
                        username != null ? username : "",
                        realName != null ? realName : "",
                        status);
            } else {
                allUsers = userRepository.findByUsernameContainingOrRealNameContaining(
                        username != null ? username : "",
                        realName != null ? realName : "");
            }
            
            // 分页处理
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, allUsers.size());
            List<User> paginatedUsers = allUsers.subList(start, end);
            
            // 转换为前端需要的格式
            List<Map<String, Object>> userList = paginatedUsers.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("realName", user.getRealName());
                userMap.put("email", user.getPhone()); // 前端使用email字段，后端实际存储的是phone
                userMap.put("phone", user.getPhone());
                userMap.put("status", user.getStatus());
                userMap.put("createTime", user.getCreateTime());
                userMap.put("updateTime", user.getUpdateTime());
                
                // 确定用户角色
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> "admin".equals(role.getCode()) || "超级管理员".equals(role.getName()));
                userMap.put("role", isAdmin ? "admin" : "user");
                
                return userMap;
            }).collect(Collectors.toList());
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", Collections.singletonMap("users", userList));
            response.put("total", allUsers.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // 获取管理员列表
    @GetMapping("/admin/list")
    public ResponseEntity<?> getAdminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> allUsers;
            
            // 根据条件查询所有用户
            if (status != null) {
                allUsers = userRepository.findByUsernameContainingOrRealNameContainingAndStatus(
                        username != null ? username : "",
                        realName != null ? realName : "",
                        status);
            } else {
                allUsers = userRepository.findByUsernameContainingOrRealNameContaining(
                        username != null ? username : "",
                        realName != null ? realName : "");
            }
            
            // 过滤出管理员
            List<User> adminUsers = allUsers.stream()
                    .filter(user -> user.getRoles().stream()
                            .anyMatch(role -> "admin".equals(role.getCode()) || "超级管理员".equals(role.getName())))
                    .collect(Collectors.toList());
            
            // 分页处理
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, adminUsers.size());
            List<User> paginatedAdmins = adminUsers.subList(start, end);
            
            // 转换为前端需要的格式
            List<Map<String, Object>> adminList = paginatedAdmins.stream().map(user -> {
                Map<String, Object> adminMap = new HashMap<>();
                adminMap.put("id", user.getId());
                adminMap.put("username", user.getUsername());
                adminMap.put("realName", user.getRealName());
                adminMap.put("email", user.getPhone()); // 前端使用email字段，后端实际存储的是phone
                adminMap.put("phone", user.getPhone());
                adminMap.put("status", user.getStatus());
                adminMap.put("createTime", user.getCreateTime());
                adminMap.put("updateTime", user.getUpdateTime());
                
                return adminMap;
            }).collect(Collectors.toList());
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", Collections.singletonMap("admins", adminList));
            response.put("total", adminUsers.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // 提升用户为管理员
    @PutMapping("/promote/{id}")
    public ResponseEntity<?> promoteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Role adminRole = roleRepository.findByCode("admin");
                
                if (adminRole == null) {
                    // 如果admin角色不存在，创建一个
                    adminRole = new Role();
                    adminRole.setName("管理员");
                    adminRole.setCode("admin");
                    adminRole.setDescription("普通管理员");
                    adminRole.setStatus(1);
                    adminRole.setCreateTime(System.currentTimeMillis());
                    adminRole.setUpdateTime(System.currentTimeMillis());
                    adminRole = roleRepository.save(adminRole);
                }
                
                // 添加管理员角色
                user.getRoles().add(adminRole);
                userRepository.save(user);
                
                response.put("code", 200);
                response.put("message", "提权成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "提权失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // 降低管理员为普通用户
    @PutMapping("/demote/{id}")
    public ResponseEntity<?> demoteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                
                // 移除管理员角色
                user.getRoles().removeIf(role -> "admin".equals(role.getCode()));
                userRepository.save(user);
                
                response.put("code", 200);
                response.put("message", "降权成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "降权失败: " + e.getMessage());
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
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                
                // 更新用户基本信息
                existingUser.setRealName((String) userData.get("realName"));
                existingUser.setPhone((String) userData.get("phone"));
                existingUser.setStatus((Integer) userData.get("status"));
                existingUser.setUpdateTime(System.currentTimeMillis());
                
                // 更新用户角色
                if (userData.containsKey("roleIds")) {
                    List<?> roleIds = (List<?>) userData.get("roleIds");
                    Set<Role> roles = new HashSet<>();
                    for (Object roleIdObj : roleIds) {
                        Long roleId;
                        // 处理类型转换，支持Integer和Long
                        if (roleIdObj instanceof Integer) {
                            roleId = ((Integer) roleIdObj).longValue();
                        } else if (roleIdObj instanceof Long) {
                            roleId = (Long) roleIdObj;
                        } else {
                            continue; // 跳过无效类型
                        }
                        Optional<Role> optionalRole = roleRepository.findById(roleId);
                        optionalRole.ifPresent(roles::add);
                    }
                    existingUser.setRoles(roles);
                }
                
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


