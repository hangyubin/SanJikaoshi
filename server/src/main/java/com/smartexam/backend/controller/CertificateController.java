package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Certificate;
import com.smartexam.backend.repository.CertificateRepository;
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
@RequestMapping("/api/certificates")
public class CertificateController {
    
    @Autowired
    private CertificateRepository certificateRepository;
    
    // 获取所有证书列表（支持分页）
    @GetMapping
    public ResponseEntity<?> getAllCertificates(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        
        // 创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        
        // 执行分页查询
        Page<Certificate> certificatePage;
        if (name != null || status != null) {
            // 这里可以添加条件查询逻辑
            certificatePage = certificateRepository.findAll(pageable);
        } else {
            certificatePage = certificateRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取证书列表成功");
        response.put("data", certificatePage.getContent());
        response.put("total", certificatePage.getTotalElements());
        response.put("pageNum", pageNum);
        response.put("pageSize", pageSize);
        response.put("pages", certificatePage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    // 根据ID获取证书详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificateById(@PathVariable Long id) {
        Optional<Certificate> optionalCertificate = certificateRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalCertificate.isPresent()) {
            response.put("code", 200);
            response.put("message", "获取证书详情成功");
            response.put("data", optionalCertificate.get());
        } else {
            response.put("code", 404);
            response.put("message", "证书不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 创建证书
    @PostMapping
    public ResponseEntity<?> createCertificate(@RequestBody Certificate certificate) {
        // 设置默认值
        certificate.setStatus(1); // 启用状态
        certificate.setCreateTime(new Date().getTime());
        certificate.setUpdateTime(new Date().getTime());
        
        // 保存证书
        Certificate savedCertificate = certificateRepository.save(certificate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建证书成功");
        response.put("data", savedCertificate);
        
        return ResponseEntity.ok(response);
    }
    
    // 更新证书
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCertificate(@PathVariable Long id, @RequestBody Certificate certificate) {
        Optional<Certificate> optionalCertificate = certificateRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalCertificate.isPresent()) {
            Certificate existingCertificate = optionalCertificate.get();
            // 更新证书信息
            existingCertificate.setName(certificate.getName());
            existingCertificate.setDescription(certificate.getDescription());
            existingCertificate.setPassScore(certificate.getPassScore());
            existingCertificate.setValidityPeriod(certificate.getValidityPeriod());
            existingCertificate.setSubject(certificate.getSubject());
            existingCertificate.setStatus(certificate.getStatus());
            existingCertificate.setUpdateTime(new Date().getTime());
            
            // 保存更新后的证书
            Certificate updatedCertificate = certificateRepository.save(existingCertificate);
            
            response.put("code", 200);
            response.put("message", "更新证书成功");
            response.put("data", updatedCertificate);
        } else {
            response.put("code", 404);
            response.put("message", "证书不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 删除证书
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificate(@PathVariable Long id) {
        Optional<Certificate> optionalCertificate = certificateRepository.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (optionalCertificate.isPresent()) {
            certificateRepository.deleteById(id);
            response.put("code", 200);
            response.put("message", "删除证书成功");
        } else {
            response.put("code", 404);
            response.put("message", "证书不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 根据科目ID获取证书列表
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<?> getCertificatesBySubjectId(@PathVariable Long subjectId) {
        List<Certificate> certificates = certificateRepository.findBySubjectId(subjectId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取证书列表成功");
        response.put("data", certificates);
        return ResponseEntity.ok(response);
    }
    
    // 根据名称获取证书
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getCertificateByName(@PathVariable String name) {
        Certificate certificate = certificateRepository.findByName(name);
        Map<String, Object> response = new HashMap<>();
        
        if (certificate != null) {
            response.put("code", 200);
            response.put("message", "获取证书成功");
            response.put("data", certificate);
        } else {
            response.put("code", 404);
            response.put("message", "证书不存在");
        }
        
        return ResponseEntity.ok(response);
    }
}