package com.smartexam.backend.controller;

import com.smartexam.backend.entity.UserCertificate;
import com.smartexam.backend.repository.UserCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-certificates")
public class UserCertificateController {
    
    @Autowired
    private UserCertificateRepository userCertificateRepository;
    
    // 获取所有用户证书关联列表
    @GetMapping
    public ResponseEntity<?> getAllUserCertificates() {
        List<UserCertificate> userCertificates = userCertificateRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取用户证书关联列表成功");
        response.put("data", userCertificates);
        return ResponseEntity.ok(response);
    }
    
    // 根据ID获取用户证书关联详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserCertificateById(@PathVariable Long id) {
        Optional<UserCertificate> optionalUserCertificate = userCertificateRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalUserCertificate.isPresent()) {
            response.put("code", 200);
            response.put("message", "获取用户证书关联详情成功");
            response.put("data", optionalUserCertificate.get());
        } else {
            response.put("code", 404);
            response.put("message", "用户证书关联不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 创建用户证书关联
    @PostMapping
    public ResponseEntity<?> createUserCertificate(@RequestBody UserCertificate userCertificate) {
        // 设置默认值
        userCertificate.setStatus(1); // 有效状态
        userCertificate.setCreateTime(new Date().getTime());
        userCertificate.setUpdateTime(new Date().getTime());
        
        // 计算过期日期
        if (userCertificate.getCertificate().getValidityPeriod() != null) {
            long issueDate = userCertificate.getIssueDate() != null ? userCertificate.getIssueDate() : new Date().getTime();
            long expireDate = issueDate + (long) userCertificate.getCertificate().getValidityPeriod() * 365 * 24 * 60 * 60 * 1000L;
            userCertificate.setExpireDate(expireDate);
        }
        
        // 保存用户证书关联
        UserCertificate savedUserCertificate = userCertificateRepository.save(userCertificate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建用户证书关联成功");
        response.put("data", savedUserCertificate);
        
        return ResponseEntity.ok(response);
    }
    
    // 更新用户证书关联
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserCertificate(@PathVariable Long id, @RequestBody UserCertificate userCertificate) {
        Optional<UserCertificate> optionalUserCertificate = userCertificateRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalUserCertificate.isPresent()) {
            UserCertificate existingUserCertificate = optionalUserCertificate.get();
            // 更新用户证书关联信息
            existingUserCertificate.setUser(userCertificate.getUser());
            existingUserCertificate.setCertificate(userCertificate.getCertificate());
            existingUserCertificate.setScore(userCertificate.getScore());
            existingUserCertificate.setIssueDate(userCertificate.getIssueDate());
            existingUserCertificate.setStatus(userCertificate.getStatus());
            existingUserCertificate.setUpdateTime(new Date().getTime());
            
            // 重新计算过期日期
            if (userCertificate.getCertificate().getValidityPeriod() != null) {
                long issueDate = userCertificate.getIssueDate() != null ? userCertificate.getIssueDate() : new Date().getTime();
                long expireDate = issueDate + (long) userCertificate.getCertificate().getValidityPeriod() * 365 * 24 * 60 * 60 * 1000L;
                existingUserCertificate.setExpireDate(expireDate);
            }
            
            // 保存更新后的用户证书关联
            UserCertificate updatedUserCertificate = userCertificateRepository.save(existingUserCertificate);
            
            response.put("code", 200);
            response.put("message", "更新用户证书关联成功");
            response.put("data", updatedUserCertificate);
        } else {
            response.put("code", 404);
            response.put("message", "用户证书关联不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 删除用户证书关联
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserCertificate(@PathVariable Long id) {
        Optional<UserCertificate> optionalUserCertificate = userCertificateRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalUserCertificate.isPresent()) {
            userCertificateRepository.deleteById(id);
            response.put("code", 200);
            response.put("message", "删除用户证书关联成功");
        } else {
            response.put("code", 404);
            response.put("message", "用户证书关联不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 根据用户ID获取证书列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCertificatesByUserId(@PathVariable Long userId) {
        List<UserCertificate> userCertificates = userCertificateRepository.findByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取用户证书列表成功");
        response.put("data", userCertificates);
        return ResponseEntity.ok(response);
    }
    
    // 根据证书ID获取用户证书列表
    @GetMapping("/certificate/{certificateId}")
    public ResponseEntity<?> getUserCertificatesByCertificateId(@PathVariable Long certificateId) {
        List<UserCertificate> userCertificates = userCertificateRepository.findByCertificateId(certificateId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取证书用户列表成功");
        response.put("data", userCertificates);
        return ResponseEntity.ok(response);
    }
    
    // 根据用户ID和证书ID获取用户证书
    @GetMapping("/user/{userId}/certificate/{certificateId}")
    public ResponseEntity<?> getUserCertificateByUserIdAndCertificateId(@PathVariable Long userId, @PathVariable Long certificateId) {
        UserCertificate userCertificate = userCertificateRepository.findByUserIdAndCertificateId(userId, certificateId);
        Map<String, Object> response = new HashMap<>();
        
        if (userCertificate != null) {
            response.put("code", 200);
            response.put("message", "获取用户证书成功");
            response.put("data", userCertificate);
        } else {
            response.put("code", 404);
            response.put("message", "用户证书不存在");
        }
        
        return ResponseEntity.ok(response);
    }
}