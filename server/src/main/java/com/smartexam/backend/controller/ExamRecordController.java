package com.smartexam.backend.controller;

import com.smartexam.backend.entity.ExamRecord;
import com.smartexam.backend.repository.ExamRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/exam-records")
public class ExamRecordController {

    @Autowired
    private ExamRecordRepository examRecordRepository;

    // 获取所有考试记录
    @GetMapping
    public ResponseEntity<?> getAllExamRecords() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamRecord> records = examRecordRepository.findAll();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", records);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据ID获取考试记录
    @GetMapping("/{id}")
    public ResponseEntity<?> getExamRecordById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ExamRecord> optionalRecord = examRecordRepository.findById(id);
            if (optionalRecord.isPresent()) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", optionalRecord.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "考试记录不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据用户ID获取考试记录列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getExamRecordsByUserId(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamRecord> records = examRecordRepository.findByUserId(userId);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", records);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据试卷ID获取考试记录列表
    @GetMapping("/paper/{examPaperId}")
    public ResponseEntity<?> getExamRecordsByExamPaperId(@PathVariable Long examPaperId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamRecord> records = examRecordRepository.findByExamPaperId(examPaperId);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", records);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据用户ID和试卷ID获取考试记录
    @GetMapping("/user/{userId}/paper/{examPaperId}")
    public ResponseEntity<?> getExamRecordsByUserIdAndExamPaperId(@PathVariable Long userId, @PathVariable Long examPaperId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamRecord> records = examRecordRepository.findByUserIdAndExamPaperId(userId, examPaperId);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", records);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据状态获取考试记录列表
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getExamRecordsByStatus(@PathVariable Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamRecord> records = examRecordRepository.findByStatus(status);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", records);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 创建考试记录
    @PostMapping
    public ResponseEntity<?> createExamRecord(@RequestBody ExamRecord record) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置默认值
            if (record.getExamTime() == null) {
                record.setExamTime(System.currentTimeMillis());
            }
            if (record.getStatus() == null) {
                record.setStatus(1); // 默认已完成
            }
            if (record.getScore() == null) {
                record.setScore(0); // 默认0分
            }
            if (record.getTotalScore() == null) {
                record.setTotalScore(100); // 默认总分100
            }
            
            ExamRecord savedRecord = examRecordRepository.save(record);
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", savedRecord);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 更新考试记录
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExamRecord(@PathVariable Long id, @RequestBody ExamRecord record) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ExamRecord> optionalRecord = examRecordRepository.findById(id);
            if (optionalRecord.isPresent()) {
                ExamRecord existingRecord = optionalRecord.get();
                
                // 更新考试记录信息
                existingRecord.setUser(record.getUser());
                existingRecord.setExamPaper(record.getExamPaper());
                existingRecord.setScore(record.getScore());
                existingRecord.setTotalScore(record.getTotalScore());
                existingRecord.setAnswers(record.getAnswers());
                existingRecord.setCorrectAnswers(record.getCorrectAnswers());
                existingRecord.setStatus(record.getStatus());
                existingRecord.setExamTime(record.getExamTime());
                existingRecord.setDuration(record.getDuration());
                
                ExamRecord updatedRecord = examRecordRepository.save(existingRecord);
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updatedRecord);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "考试记录不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 删除考试记录
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExamRecord(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ExamRecord> optionalRecord = examRecordRepository.findById(id);
            if (optionalRecord.isPresent()) {
                examRecordRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "考试记录不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量删除考试记录
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteExamRecords(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            examRecordRepository.deleteAllById(ids);
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