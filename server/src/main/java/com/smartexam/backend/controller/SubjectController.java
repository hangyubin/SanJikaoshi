package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Subject;
import com.smartexam.backend.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    // 获取所有科目
    @GetMapping
    public ResponseEntity<?> getAllSubjects() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Subject> subjects = subjectRepository.findAll();
            response.put("code", 200);
            response.put("message", "获取科目列表成功");
            response.put("data", subjects);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取科目列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据ID获取科目
    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Subject> optionalSubject = subjectRepository.findById(id);
            if (optionalSubject.isPresent()) {
                response.put("code", 200);
                response.put("message", "获取科目成功");
                response.put("data", optionalSubject.get());
            } else {
                response.put("code", 404);
                response.put("message", "科目不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取科目失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 创建科目
    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody Subject subject) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置默认值
            if (subject.getStatus() == null) {
                subject.setStatus(1); // 默认启用状态
            }
            if (subject.getCreateTime() == null) {
                subject.setCreateTime(System.currentTimeMillis());
            }
            if (subject.getUpdateTime() == null) {
                subject.setUpdateTime(System.currentTimeMillis());
            }

            // 保存科目
            Subject savedSubject = subjectRepository.save(subject);
            response.put("code", 200);
            response.put("message", "创建科目成功");
            response.put("data", savedSubject);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建科目失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 更新科目
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @RequestBody Subject subject) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Subject> optionalSubject = subjectRepository.findById(id);
            if (optionalSubject.isPresent()) {
                Subject existingSubject = optionalSubject.get();
                // 更新科目信息
                existingSubject.setName(subject.getName());
                existingSubject.setDescription(subject.getDescription());
                existingSubject.setCode(subject.getCode());
                existingSubject.setStatus(subject.getStatus());
                existingSubject.setUpdateTime(System.currentTimeMillis());

                // 保存更新后的科目
                Subject updatedSubject = subjectRepository.save(existingSubject);
                response.put("code", 200);
                response.put("message", "更新科目成功");
                response.put("data", updatedSubject);
            } else {
                response.put("code", 404);
                response.put("message", "科目不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新科目失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 删除科目
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Subject> optionalSubject = subjectRepository.findById(id);
            if (optionalSubject.isPresent()) {
                subjectRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除科目成功");
            } else {
                response.put("code", 404);
                response.put("message", "科目不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除科目失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据状态获取科目
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getSubjectsByStatus(@PathVariable Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Subject> subjects = subjectRepository.findByStatus(status);
            response.put("code", 200);
            response.put("message", "获取科目列表成功");
            response.put("data", subjects);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取科目列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 初始化系统默认科目（基本理论、基础知识、基本技能）
    @PostMapping("/init-default")
    public ResponseEntity<?> initDefaultSubjects() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 检查是否已存在默认科目
            List<Subject> existingSubjects = subjectRepository.findAll();
            if (!existingSubjects.isEmpty()) {
                response.put("code", 200);
                response.put("message", "默认科目已存在");
                response.put("data", existingSubjects);
                return ResponseEntity.ok(response);
            }

            long currentTime = System.currentTimeMillis();

            // 创建基本理论科目
            Subject basicTheory = new Subject();
            basicTheory.setName("基本理论");
            basicTheory.setDescription("医疗卫生系统三基考试-基本理论部分");
            basicTheory.setCode("BASIC_THEORY");
            basicTheory.setStatus(1);
            basicTheory.setCreateTime(currentTime);
            basicTheory.setUpdateTime(currentTime);

            // 创建基础知识科目
            Subject basicKnowledge = new Subject();
            basicKnowledge.setName("基础知识");
            basicKnowledge.setDescription("医疗卫生系统三基考试-基础知识部分");
            basicKnowledge.setCode("BASIC_KNOWLEDGE");
            basicKnowledge.setStatus(1);
            basicKnowledge.setCreateTime(currentTime);
            basicKnowledge.setUpdateTime(currentTime);

            // 创建基本技能科目
            Subject basicSkills = new Subject();
            basicSkills.setName("基本技能");
            basicSkills.setDescription("医疗卫生系统三基考试-基本技能部分");
            basicSkills.setCode("BASIC_SKILLS");
            basicSkills.setStatus(1);
            basicSkills.setCreateTime(currentTime);
            basicSkills.setUpdateTime(currentTime);

            // 保存所有科目
            List<Subject> defaultSubjects = new ArrayList<>();
            defaultSubjects.add(basicTheory);
            defaultSubjects.add(basicKnowledge);
            defaultSubjects.add(basicSkills);
            defaultSubjects = subjectRepository.saveAll(defaultSubjects);

            response.put("code", 200);
            response.put("message", "初始化默认科目成功");
            response.put("data", defaultSubjects);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "初始化默认科目失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}


