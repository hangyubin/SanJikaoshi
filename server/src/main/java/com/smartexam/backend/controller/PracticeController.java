package com.smartexam.backend.controller;

import com.smartexam.backend.entity.PracticeRecord;
import com.smartexam.backend.repository.PracticeRecordRepository;
import com.smartexam.backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/practice")
public class PracticeController {

    @Autowired
    private PracticeRecordRepository practiceRecordRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // 保存练习记录
    @PostMapping("/save")
    public ResponseEntity<?> savePracticeRecord(@RequestBody PracticeRecord record) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置默认值
            if (record.getCreateTime() == null) {
                record.setCreateTime(System.currentTimeMillis());
            }
            if (record.getUpdateTime() == null) {
                record.setUpdateTime(System.currentTimeMillis());
            }
            if (record.getPracticeTime() == null) {
                record.setPracticeTime(System.currentTimeMillis());
            }

            // 检查题目是否存在
            if (record.getQuestion() == null || record.getQuestion().getId() == null) {
                response.put("code", 400);
                response.put("message", "题目信息不能为空");
                return ResponseEntity.ok(response);
            }

            // 检查用户是否存在
            if (record.getUser() == null || record.getUser().getId() == null) {
                response.put("code", 400);
                response.put("message", "用户信息不能为空");
                return ResponseEntity.ok(response);
            }

            // 计算是否正确
            if (record.getAnswer() != null && record.getCorrectAnswer() != null) {
                record.setIsCorrect(record.getAnswer().equals(record.getCorrectAnswer()));
            }

            PracticeRecord savedRecord = practiceRecordRepository.save(record);

            response.put("code", 200);
            response.put("message", "练习记录保存成功");
            response.put("data", savedRecord);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量保存练习记录
    @PostMapping("/batch-save")
    public ResponseEntity<?> batchSavePracticeRecords(@RequestBody List<PracticeRecord> records) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (records == null || records.isEmpty()) {
                response.put("code", 400);
                response.put("message", "练习记录列表不能为空");
                return ResponseEntity.ok(response);
            }

            long currentTime = System.currentTimeMillis();

            // 设置默认值
            for (PracticeRecord record : records) {
                if (record.getCreateTime() == null) {
                    record.setCreateTime(currentTime);
                }
                if (record.getUpdateTime() == null) {
                    record.setUpdateTime(currentTime);
                }
                if (record.getPracticeTime() == null) {
                    record.setPracticeTime(currentTime);
                }

                // 计算是否正确
                if (record.getAnswer() != null && record.getCorrectAnswer() != null) {
                    record.setIsCorrect(record.getAnswer().equals(record.getCorrectAnswer()));
                }
            }

            List<PracticeRecord> savedRecords = practiceRecordRepository.saveAll(records);

            response.put("code", 200);
            response.put("message", "批量保存成功");
            response.put("data", savedRecords);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量保存失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 获取用户练习记录
    @GetMapping("/records/{userId}")
    public ResponseEntity<?> getPracticeRecords(@PathVariable Long userId,
                                               @RequestParam(required = false) Integer subjectId,
                                               @RequestParam(required = false) Integer type,
                                               @RequestParam(required = false) Integer difficulty,
                                               @RequestParam(required = false) Boolean isCorrect) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PracticeRecord> records;

            // 根据不同条件查询
            if (isCorrect != null) {
                if (subjectId != null) {
                    records = practiceRecordRepository.findByUserIdAndIsCorrectAndSubjectId(userId, isCorrect, subjectId);
                } else if (type != null) {
                    records = practiceRecordRepository.findByUserIdAndIsCorrectAndType(userId, isCorrect, type);
                } else if (difficulty != null) {
                    records = practiceRecordRepository.findByUserIdAndIsCorrectAndDifficulty(userId, isCorrect, difficulty);
                } else {
                    records = practiceRecordRepository.findByUserIdAndIsCorrect(userId, isCorrect);
                }
            } else if (subjectId != null) {
                records = practiceRecordRepository.findByUserIdAndSubjectId(userId, subjectId);
            } else if (type != null) {
                records = practiceRecordRepository.findByUserIdAndType(userId, type);
            } else if (difficulty != null) {
                records = practiceRecordRepository.findByUserIdAndDifficulty(userId, difficulty);
            } else {
                records = practiceRecordRepository.findByUserId(userId);
            }

            // 按练习时间倒序排序
            records.sort((a, b) -> Long.compare(b.getPracticeTime(), a.getPracticeTime()));

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

    // 获取用户错题列表
    @GetMapping("/wrong-questions/{userId}")
    public ResponseEntity<?> getWrongQuestions(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取所有错题
            List<PracticeRecord> wrongRecords = practiceRecordRepository.findByUserIdAndIsCorrect(userId, false);

            // 按练习时间倒序排序
            wrongRecords.sort((a, b) -> Long.compare(b.getPracticeTime(), a.getPracticeTime()));

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", wrongRecords);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 获取用户错题按科目分类
    @GetMapping("/wrong-questions/subject/{userId}")
    public ResponseEntity<?> getWrongQuestionsBySubject(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取所有错题
            List<PracticeRecord> wrongRecords = practiceRecordRepository.findByUserIdAndIsCorrect(userId, false);

            // 按科目ID分组
            Map<Integer, List<PracticeRecord>> subjectGrouped = wrongRecords.stream()
                    .collect(Collectors.groupingBy(PracticeRecord::getSubjectId));

            // 转换为更友好的格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<Integer, List<PracticeRecord>> entry : subjectGrouped.entrySet()) {
                Map<String, Object> group = new HashMap<>();
                group.put("subjectId", entry.getKey());
                group.put("count", entry.getValue().size());
                group.put("records", entry.getValue());
                result.add(group);
            }

            // 按错题数量倒序排序
            result.sort((a, b) -> Integer.compare((Integer) b.get("count"), (Integer) a.get("count")));

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 获取用户错题按题型分类
    @GetMapping("/wrong-questions/type/{userId}")
    public ResponseEntity<?> getWrongQuestionsByType(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取所有错题
            List<PracticeRecord> wrongRecords = practiceRecordRepository.findByUserIdAndIsCorrect(userId, false);

            // 按题型分组
            Map<Integer, List<PracticeRecord>> typeGrouped = wrongRecords.stream()
                    .collect(Collectors.groupingBy(PracticeRecord::getType));

            // 转换为更友好的格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<Integer, List<PracticeRecord>> entry : typeGrouped.entrySet()) {
                Map<String, Object> group = new HashMap<>();
                group.put("type", entry.getKey());
                group.put("count", entry.getValue().size());
                group.put("records", entry.getValue());
                result.add(group);
            }

            // 按错题数量倒序排序
            result.sort((a, b) -> Integer.compare((Integer) b.get("count"), (Integer) a.get("count")));

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 获取用户错题按难度分类
    @GetMapping("/wrong-questions/difficulty/{userId}")
    public ResponseEntity<?> getWrongQuestionsByDifficulty(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取所有错题
            List<PracticeRecord> wrongRecords = practiceRecordRepository.findByUserIdAndIsCorrect(userId, false);

            // 按难度分组
            Map<Integer, List<PracticeRecord>> difficultyGrouped = wrongRecords.stream()
                    .collect(Collectors.groupingBy(PracticeRecord::getDifficulty));

            // 转换为更友好的格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<Integer, List<PracticeRecord>> entry : difficultyGrouped.entrySet()) {
                Map<String, Object> group = new HashMap<>();
                group.put("difficulty", entry.getKey());
                group.put("count", entry.getValue().size());
                group.put("records", entry.getValue());
                result.add(group);
            }

            // 按难度升序排序
            result.sort(Comparator.comparingInt(a -> (Integer) a.get("difficulty")));

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 获取练习统计信息
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getPracticeStats(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取所有练习记录
            List<PracticeRecord> allRecords = practiceRecordRepository.findByUserId(userId);
            // 获取正确记录
            List<PracticeRecord> correctRecords = practiceRecordRepository.findByUserIdAndIsCorrect(userId, true);
            // 获取错误记录
            List<PracticeRecord> wrongRecords = practiceRecordRepository.findByUserIdAndIsCorrect(userId, false);

            // 计算统计信息
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", allRecords.size());
            stats.put("correctCount", correctRecords.size());
            stats.put("wrongCount", wrongRecords.size());
            stats.put("correctRate", allRecords.size() > 0 ? (double) correctRecords.size() / allRecords.size() : 0);
            stats.put("wrongRate", allRecords.size() > 0 ? (double) wrongRecords.size() / allRecords.size() : 0);

            // 按科目统计
            Map<Integer, Long> subjectStats = allRecords.stream()
                    .collect(Collectors.groupingBy(PracticeRecord::getSubjectId, Collectors.counting()));
            stats.put("subjectStats", subjectStats);

            // 按题型统计
            Map<Integer, Long> typeStats = allRecords.stream()
                    .collect(Collectors.groupingBy(PracticeRecord::getType, Collectors.counting()));
            stats.put("typeStats", typeStats);

            // 按难度统计
            Map<Integer, Long> difficultyStats = allRecords.stream()
                    .collect(Collectors.groupingBy(PracticeRecord::getDifficulty, Collectors.counting()));
            stats.put("difficultyStats", difficultyStats);

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}


