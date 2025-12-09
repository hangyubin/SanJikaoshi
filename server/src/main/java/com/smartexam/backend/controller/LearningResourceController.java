package com.smartexam.backend.controller;

import com.smartexam.backend.entity.LearningResource;
import com.smartexam.backend.repository.LearningResourceRepository;
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
@RequestMapping("/api/learning-resources")
public class LearningResourceController {
    
    @Autowired
    private LearningResourceRepository learningResourceRepository;
    
    // 获取所有学习资源列表（支持分页）
    @GetMapping
    public ResponseEntity<?> getAllLearningResources(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        
        // 创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        
        // 执行分页查询
        Page<LearningResource> resourcePage;
        if (title != null || type != null || status != null) {
            // 这里可以添加条件查询逻辑
            resourcePage = learningResourceRepository.findAll(pageable);
        } else {
            resourcePage = learningResourceRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取学习资源列表成功");
        response.put("data", resourcePage.getContent());
        response.put("total", resourcePage.getTotalElements());
        response.put("pageNum", pageNum);
        response.put("pageSize", pageSize);
        response.put("pages", resourcePage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    // 根据ID获取学习资源详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getLearningResourceById(@PathVariable Long id) {
        Optional<LearningResource> optionalLearningResource = learningResourceRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalLearningResource.isPresent()) {
            response.put("code", 200);
            response.put("message", "获取学习资源详情成功");
            response.put("data", optionalLearningResource.get());
        } else {
            response.put("code", 404);
            response.put("message", "学习资源不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 创建学习资源
    @PostMapping
    public ResponseEntity<?> createLearningResource(@RequestBody LearningResource learningResource) {
        // 设置默认值
        learningResource.setStatus(1); // 启用状态
        learningResource.setCreateTime(new Date().getTime());
        learningResource.setUpdateTime(new Date().getTime());
        
        // 保存学习资源
        LearningResource savedLearningResource = learningResourceRepository.save(learningResource);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建学习资源成功");
        response.put("data", savedLearningResource);
        
        return ResponseEntity.ok(response);
    }
    
    // 更新学习资源
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLearningResource(@PathVariable Long id, @RequestBody LearningResource learningResource) {
        Optional<LearningResource> optionalLearningResource = learningResourceRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalLearningResource.isPresent()) {
            LearningResource existingLearningResource = optionalLearningResource.get();
            // 更新学习资源信息
            existingLearningResource.setTitle(learningResource.getTitle());
            existingLearningResource.setDescription(learningResource.getDescription());
            existingLearningResource.setType(learningResource.getType());
            existingLearningResource.setUrl(learningResource.getUrl());
            existingLearningResource.setFileType(learningResource.getFileType());
            existingLearningResource.setFileSize(learningResource.getFileSize());
            existingLearningResource.setSubject(learningResource.getSubject());
            existingLearningResource.setCreateBy(learningResource.getCreateBy());
            existingLearningResource.setStatus(learningResource.getStatus());
            existingLearningResource.setUpdateTime(new Date().getTime());
            
            // 保存更新后的学习资源
            LearningResource updatedLearningResource = learningResourceRepository.save(existingLearningResource);
            
            response.put("code", 200);
            response.put("message", "更新学习资源成功");
            response.put("data", updatedLearningResource);
        } else {
            response.put("code", 404);
            response.put("message", "学习资源不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 删除学习资源
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLearningResource(@PathVariable Long id) {
        Optional<LearningResource> optionalLearningResource = learningResourceRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalLearningResource.isPresent()) {
            learningResourceRepository.deleteById(id);
            response.put("code", 200);
            response.put("message", "删除学习资源成功");
        } else {
            response.put("code", 404);
            response.put("message", "学习资源不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 根据科目ID获取学习资源列表
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<?> getLearningResourcesBySubjectId(@PathVariable Long subjectId) {
        List<LearningResource> learningResources = learningResourceRepository.findBySubjectId(subjectId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取科目学习资源列表成功");
        response.put("data", learningResources);
        return ResponseEntity.ok(response);
    }
    
    // 根据资源类型获取学习资源列表
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getLearningResourcesByType(@PathVariable String type) {
        List<LearningResource> learningResources = learningResourceRepository.findByType(type);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取类型学习资源列表成功");
        response.put("data", learningResources);
        return ResponseEntity.ok(response);
    }
    
    // 根据创建者ID获取学习资源列表
    @GetMapping("/create-by/{createById}")
    public ResponseEntity<?> getLearningResourcesByCreateById(@PathVariable Long createById) {
        List<LearningResource> learningResources = learningResourceRepository.findByCreateById(createById);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取创建者学习资源列表成功");
        response.put("data", learningResources);
        return ResponseEntity.ok(response);
    }
}