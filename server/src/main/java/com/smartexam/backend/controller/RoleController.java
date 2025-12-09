package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Role;
import com.smartexam.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    // 获取所有角色
    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Role> roles = roleRepository.findAll();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据ID获取角色
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Role> optionalRole = roleRepository.findById(id);
            if (optionalRole.isPresent()) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", optionalRole.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "角色不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据状态获取角色列表
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getRolesByStatus(@PathVariable Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Role> roles = roleRepository.findByStatus(status);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 创建角色
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置默认值
            if (role.getCreateTime() == null) {
                role.setCreateTime(System.currentTimeMillis());
            }
            if (role.getUpdateTime() == null) {
                role.setUpdateTime(System.currentTimeMillis());
            }
            if (role.getStatus() == null) {
                role.setStatus(1); // 默认启用
            }
            
            Role savedRole = roleRepository.save(role);
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", savedRole);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 更新角色
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Role> optionalRole = roleRepository.findById(id);
            if (optionalRole.isPresent()) {
                Role existingRole = optionalRole.get();
                
                // 更新角色信息
                existingRole.setName(role.getName());
                existingRole.setDescription(role.getDescription());
                existingRole.setMenus(role.getMenus());
                existingRole.setStatus(role.getStatus());
                existingRole.setUpdateTime(System.currentTimeMillis());
                
                Role updatedRole = roleRepository.save(existingRole);
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updatedRole);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "角色不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 删除角色
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Role> optionalRole = roleRepository.findById(id);
            if (optionalRole.isPresent()) {
                roleRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "角色不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量删除角色
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteRoles(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            roleRepository.deleteAllById(ids);
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