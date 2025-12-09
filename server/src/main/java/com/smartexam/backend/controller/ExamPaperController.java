package com.smartexam.backend.controller;

import com.smartexam.backend.entity.ExamPaper;
import com.smartexam.backend.repository.ExamPaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/papers")
public class ExamPaperController {

    @Autowired
    private ExamPaperRepository examPaperRepository;

    // 获取所有试卷
    @GetMapping
    public ResponseEntity<?> getAllPapers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamPaper> papers = examPaperRepository.findAll();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", papers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据ID获取试卷
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaperById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ExamPaper> optionalPaper = examPaperRepository.findById(id);
            if (optionalPaper.isPresent()) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", optionalPaper.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "试卷不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据科目ID获取试卷
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<?> getPapersBySubjectId(@PathVariable Long subjectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamPaper> papers = examPaperRepository.findBySubjectId(subjectId);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", papers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据创建者ID获取试卷
    @GetMapping("/creator/{createById}")
    public ResponseEntity<?> getPapersByCreateById(@PathVariable Long createById) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamPaper> papers = examPaperRepository.findByCreateById(createById);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", papers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据状态获取试卷
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPapersByStatus(@PathVariable Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamPaper> papers = examPaperRepository.findByStatus(status);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", papers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 创建试卷
    @PostMapping
    public ResponseEntity<?> createPaper(@RequestBody ExamPaper paper) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置默认值
            if (paper.getCreateTime() == null) {
                paper.setCreateTime(System.currentTimeMillis());
            }
            if (paper.getUpdateTime() == null) {
                paper.setUpdateTime(System.currentTimeMillis());
            }
            if (paper.getStatus() == null) {
                paper.setStatus(1); // 默认启用
            }
            
            ExamPaper savedPaper = examPaperRepository.save(paper);
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", savedPaper);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 更新试卷
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaper(@PathVariable Long id, @RequestBody ExamPaper paper) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ExamPaper> optionalPaper = examPaperRepository.findById(id);
            if (optionalPaper.isPresent()) {
                ExamPaper existingPaper = optionalPaper.get();
                
                // 更新试卷信息
                existingPaper.setTitle(paper.getTitle());
                existingPaper.setDescription(paper.getDescription());
                existingPaper.setSubject(paper.getSubject());
                existingPaper.setTotalScore(paper.getTotalScore());
                existingPaper.setQuestionCount(paper.getQuestionCount());
                existingPaper.setDuration(paper.getDuration());
                existingPaper.setQuestions(paper.getQuestions());
                existingPaper.setStatus(paper.getStatus());
                existingPaper.setCreateBy(paper.getCreateBy());
                existingPaper.setUpdateTime(System.currentTimeMillis());
                
                ExamPaper updatedPaper = examPaperRepository.save(existingPaper);
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updatedPaper);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "试卷不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 删除试卷
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaper(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ExamPaper> optionalPaper = examPaperRepository.findById(id);
            if (optionalPaper.isPresent()) {
                examPaperRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "试卷不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量删除试卷
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeletePapers(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            examPaperRepository.deleteAllById(ids);
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