package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Department;
import com.smartexam.backend.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    // 获取所有科室列表（支持分页）
    @GetMapping
    public ResponseEntity<?> getAllDepartments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer status) {
        
        // 创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        
        // 执行分页查询
        Page<Department> departmentPage;
        if (name != null || code != null || status != null) {
            // 这里可以添加条件查询逻辑
            departmentPage = departmentRepository.findAll(pageable);
        } else {
            departmentPage = departmentRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取科室列表成功");
        response.put("data", departmentPage.getContent());
        response.put("total", departmentPage.getTotalElements());
        response.put("pageNum", pageNum);
        response.put("pageSize", pageSize);
        response.put("pages", departmentPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    // 根据ID获取科室详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalDepartment.isPresent()) {
            response.put("code", 200);
            response.put("message", "获取科室详情成功");
            response.put("data", optionalDepartment.get());
        } else {
            response.put("code", 404);
            response.put("message", "科室不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 创建科室
    @PostMapping
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        // 设置默认值
        department.setStatus(1); // 启用状态
        department.setCreateTime(new Date().getTime());
        department.setUpdateTime(new Date().getTime());
        
        // 保存科室
        Department savedDepartment = departmentRepository.save(department);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建科室成功");
        response.put("data", savedDepartment);
        
        return ResponseEntity.ok(response);
    }
    
    // 更新科室
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalDepartment.isPresent()) {
            Department existingDepartment = optionalDepartment.get();
            // 更新科室信息
            existingDepartment.setName(department.getName());
            existingDepartment.setCode(department.getCode());
            existingDepartment.setDescription(department.getDescription());
            existingDepartment.setStatus(department.getStatus());
            existingDepartment.setUpdateTime(new Date().getTime());
            
            // 保存更新后的科室
            Department updatedDepartment = departmentRepository.save(existingDepartment);
            
            response.put("code", 200);
            response.put("message", "更新科室成功");
            response.put("data", updatedDepartment);
        } else {
            response.put("code", 404);
            response.put("message", "科室不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 删除科室
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalDepartment.isPresent()) {
            departmentRepository.deleteById(id);
            response.put("code", 200);
            response.put("message", "删除科室成功");
        } else {
            response.put("code", 404);
            response.put("message", "科室不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 根据编码获取科室
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getDepartmentByCode(@PathVariable String code) {
        Department department = departmentRepository.findByCode(code);
        Map<String, Object> response = new HashMap<>();
        
        if (department != null) {
            response.put("code", 200);
            response.put("message", "获取科室成功");
            response.put("data", department);
        } else {
            response.put("code", 404);
            response.put("message", "科室不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 根据名称获取科室
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getDepartmentByName(@PathVariable String name) {
        Department department = departmentRepository.findByName(name);
        Map<String, Object> response = new HashMap<>();
        
        if (department != null) {
            response.put("code", 200);
            response.put("message", "获取科室成功");
            response.put("data", department);
        } else {
            response.put("code", 404);
            response.put("message", "科室不存在");
        }
        
        return ResponseEntity.ok(response);
    }
}