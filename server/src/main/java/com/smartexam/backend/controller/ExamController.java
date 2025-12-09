package com.smartexam.backend.controller;

import com.smartexam.backend.entity.ExamPaper;
import com.smartexam.backend.entity.ExamRecord;
import com.smartexam.backend.entity.Question;
import com.smartexam.backend.entity.User;
import com.smartexam.backend.repository.ExamPaperRepository;
import com.smartexam.backend.repository.ExamRecordRepository;
import com.smartexam.backend.repository.QuestionRepository;
import com.smartexam.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    private ExamPaperRepository examPaperRepository;

    @Autowired
    private ExamRecordRepository examRecordRepository;

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private UserRepository userRepository;

    // 开始考试
    @PostMapping("/start/{paperId}")
    public ResponseEntity<?> startExam(@PathVariable Long paperId, @RequestBody Map<String, Long> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = request.get("userId");
            if (userId == null) {
                response.put("code", 400);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.ok(response);
            }

            // 获取试卷信息
            Optional<ExamPaper> optionalPaper = examPaperRepository.findById(paperId);
            if (!optionalPaper.isPresent()) {
                response.put("code", 404);
                response.put("message", "试卷不存在");
                return ResponseEntity.ok(response);
            }

            ExamPaper paper = optionalPaper.get();

            // 检查是否已经参加过该考试
            List<ExamRecord> existingRecords = examRecordRepository.findByUserIdAndExamPaperId(userId, paperId);
            if (!existingRecords.isEmpty()) {
                ExamRecord latestRecord = existingRecords.get(existingRecords.size() - 1);
                if (latestRecord.getStatus() == 2) {
                    response.put("code", 400);
                    response.put("message", "您已在该考试中");
                    response.put("data", latestRecord);
                    return ResponseEntity.ok(response);
                }
            }

            // 创建考试记录
            ExamRecord record = new ExamRecord();
            record.setUser(userRepository.findById(userId).orElse(null));
            record.setExamPaper(paper);
            record.setTotalScore(paper.getTotalScore());
            record.setStatus(2); // 进行中
            record.setStartTime(System.currentTimeMillis());
            record.setEndTime(System.currentTimeMillis() + paper.getDuration() * 60 * 1000); // 计算结束时间
            record.setCreateTime(System.currentTimeMillis());
            record.setUpdateTime(System.currentTimeMillis());

            ExamRecord savedRecord = examRecordRepository.save(record);

            // 组装返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("recordId", savedRecord.getId());
            data.put("paper", paper);
            data.put("startTime", savedRecord.getStartTime());
            data.put("endTime", savedRecord.getEndTime());
            data.put("duration", paper.getDuration());

            response.put("code", 200);
            response.put("message", "考试开始成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "开始考试失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 提交考试
    @PostMapping("/submit/{recordId}")
    public ResponseEntity<?> submitExam(@PathVariable Long recordId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取请求参数
            String answerJson = (String) request.get("answers");
            if (answerJson == null || answerJson.isEmpty()) {
                response.put("code", 400);
                response.put("message", "答案不能为空");
                return ResponseEntity.ok(response);
            }

            // 获取考试记录
            Optional<ExamRecord> optionalRecord = examRecordRepository.findById(recordId);
            if (!optionalRecord.isPresent()) {
                response.put("code", 404);
                response.put("message", "考试记录不存在");
                return ResponseEntity.ok(response);
            }

            ExamRecord record = optionalRecord.get();

            // 检查考试状态
            if (record.getStatus() != 2) {
                response.put("code", 400);
                response.put("message", "考试已结束或未开始");
                return ResponseEntity.ok(response);
            }

            // 检查是否超时
            long currentTime = System.currentTimeMillis();
            if (currentTime > record.getEndTime()) {
                response.put("code", 400);
                response.put("message", "考试已超时");
                return ResponseEntity.ok(response);
            }

            // 计算得分
            Integer score = calculateScore(record.getExamPaper().getId(), answerJson);

            // 更新考试记录
            record.setAnswerJson(answerJson);
            record.setObtainedScore(score);
            record.setStatus(3); // 已完成
            record.setSubmitTime(currentTime);
            record.setUpdateTime(currentTime);

            // 生成结果JSON
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("score", score);
            resultMap.put("totalScore", record.getTotalScore());
            resultMap.put("pass", score >= record.getTotalScore() * 0.6); // 60分及格
            resultMap.put("submitTime", record.getSubmitTime());
            resultMap.put("duration", (record.getSubmitTime() - record.getStartTime()) / 1000 / 60); // 考试时长（分钟）

            record.setResultJson(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(resultMap));

            ExamRecord updatedRecord = examRecordRepository.save(record);

            response.put("code", 200);
            response.put("message", "考试提交成功");
            response.put("data", updatedRecord);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "提交考试失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 自动评分
    private Integer calculateScore(Long paperId, String answerJson) throws Exception {
        // 获取试卷
        Optional<ExamPaper> optionalPaper = examPaperRepository.findById(paperId);
        if (!optionalPaper.isPresent()) {
            return 0;
        }

        ExamPaper paper = optionalPaper.get();

        // 解析答案
        Map<Long, String> userAnswers = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(answerJson, new com.fasterxml.jackson.core.type.TypeReference<Map<Long, String>>() {});

        // 获取所有题目
        Set<Question> questions = paper.getQuestions();
        if (questions.isEmpty()) {
            return 0;
        }

        // 计算得分
        int totalScore = 0;
        for (Question question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getAnswer())) {
                totalScore += question.getScore() != null ? question.getScore() : 10;
            }
        }

        return totalScore;
    }

    // 获取考试详情
    @GetMapping("/detail/{recordId}")
    public ResponseEntity<?> getExamDetail(@PathVariable Long recordId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ExamRecord> optionalRecord = examRecordRepository.findById(recordId);
            if (!optionalRecord.isPresent()) {
                response.put("code", 404);
                response.put("message", "考试记录不存在");
                return ResponseEntity.ok(response);
            }

            ExamRecord record = optionalRecord.get();
            ExamPaper paper = record.getExamPaper();

            // 组装返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("record", record);
            data.put("paper", paper);
            data.put("questions", paper.getQuestions());

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 获取用户考试列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserExams(@PathVariable Long userId, @RequestParam(required = false) Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExamRecord> records;
            if (status != null) {
                records = examRecordRepository.findByUserIdAndStatus(userId, status);
            } else {
                records = examRecordRepository.findByUserId(userId);
            }

            // 按时间倒序排序
            records.sort((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()));

            // 组装返回数据，包含试卷信息
            List<Map<String, Object>> examList = records.stream().map(record -> {
                Map<String, Object> examMap = new HashMap<>();
                examMap.put("record", record);
                examMap.put("paper", record.getExamPaper());
                return examMap;
            }).collect(Collectors.toList());

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", examList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 获取可参加的考试列表
    @GetMapping("/available/{userId}")
    public ResponseEntity<?> getAvailableExams(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取所有启用的试卷
            List<ExamPaper> papers = examPaperRepository.findByStatus(1);
            
            // 获取用户已参加的试卷ID
            List<ExamRecord> userRecords = examRecordRepository.findAll();
            Set<Long> participatedPaperIds = userRecords.stream()
                    .filter(record -> record.getUser() != null && record.getUser().getId().equals(userId))
                    .map(record -> record.getExamPaper().getId())
                    .collect(Collectors.toSet());

            // 过滤出用户未参加的试卷
            List<ExamPaper> availablePapers = papers.stream()
                    .filter(paper -> !participatedPaperIds.contains(paper.getId()))
                    .collect(Collectors.toList());

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", availablePapers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 检查考试状态
    @GetMapping("/status/{recordId}")
    public ResponseEntity<?> checkExamStatus(@PathVariable Long recordId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<ExamRecord> optionalRecord = examRecordRepository.findById(recordId);
            if (!optionalRecord.isPresent()) {
                response.put("code", 404);
                response.put("message", "考试记录不存在");
                return ResponseEntity.ok(response);
            }

            ExamRecord record = optionalRecord.get();
            long currentTime = System.currentTimeMillis();

            // 检查是否超时
            boolean timeout = currentTime > record.getEndTime() && record.getStatus() == 2;
            if (timeout) {
                // 更新为已过期状态
                record.setStatus(4);
                record.setUpdateTime(currentTime);
                examRecordRepository.save(record);
            }

            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("status", record.getStatus());
            statusMap.put("timeout", timeout);
            statusMap.put("remainingTime", Math.max(0, record.getEndTime() - currentTime) / 1000); // 剩余时间（秒）

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", statusMap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}