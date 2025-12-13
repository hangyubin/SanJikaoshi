package com.smartexam.backend.controller;

import com.smartexam.backend.entity.ExamPaper;
import com.smartexam.backend.entity.Question;
import com.smartexam.backend.repository.ExamPaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
                existingPaper.setName(paper.getName());
                existingPaper.setDescription(paper.getDescription());
                existingPaper.setType(paper.getType());
                existingPaper.setDifficulty(paper.getDifficulty());
                existingPaper.setTotalQuestions(paper.getTotalQuestions());
                existingPaper.setTotalScore(paper.getTotalScore());
                existingPaper.setDuration(paper.getDuration());
                existingPaper.setSubject(paper.getSubject());
                existingPaper.setQuestions(paper.getQuestions());
                existingPaper.setStatus(paper.getStatus());
                existingPaper.setRemark(paper.getRemark());
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
    
    // 根据题型比例生成试卷
    @PostMapping("/generate")
    public ResponseEntity<?> generatePaper(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取请求参数
            Long paperId = Long.parseLong(request.get("paperId").toString());
            Integer totalQuestions = Integer.parseInt(request.get("totalQuestions").toString());
            Map<String, Integer> questionTypeDistribution = (Map<String, Integer>) request.get("questionTypeDistribution");
            
            // 获取题库信息
            Optional<ExamPaper> optionalPaper = examPaperRepository.findById(paperId);
            if (!optionalPaper.isPresent()) {
                response.put("code", 404);
                response.put("message", "题库不存在");
                return ResponseEntity.ok(response);
            }
            ExamPaper templatePaper = optionalPaper.get();
            
            // 创建新试卷
            ExamPaper generatedPaper = new ExamPaper();
            generatedPaper.setName(templatePaper.getName() + "_生成卷");
            generatedPaper.setDescription(templatePaper.getDescription());
            generatedPaper.setType(templatePaper.getType());
            generatedPaper.setDifficulty(templatePaper.getDifficulty());
            generatedPaper.setTotalQuestions(totalQuestions);
            generatedPaper.setDuration(templatePaper.getDuration());
            generatedPaper.setSubject(templatePaper.getSubject());
            generatedPaper.setStatus(1); // 默认启用
            generatedPaper.setCreateTime(System.currentTimeMillis());
            generatedPaper.setUpdateTime(System.currentTimeMillis());
            
            // 获取题库中的所有题目
            Set<Question> allQuestions = templatePaper.getQuestions();
            if (allQuestions == null || allQuestions.isEmpty()) {
                response.put("code", 400);
                response.put("message", "题库中没有题目");
                return ResponseEntity.ok(response);
            }
            
            // 将Set转换为List以便随机选择
            List<Question> questionList = new ArrayList<>(allQuestions);
            
            // 根据题型比例选择题目
            Set<Question> selectedQuestions = new HashSet<>();
            int totalSelected = 0;
            int totalScore = 0;
            
            // 遍历题型比例，选择对应数量的题目
            for (Map.Entry<String, Integer> entry : questionTypeDistribution.entrySet()) {
                Integer questionType = Integer.parseInt(entry.getKey());
                Integer percentage = entry.getValue();
                
                // 计算该题型需要的题目数量
                int count = (int) Math.round(totalQuestions * percentage / 100.0);
                if (count == 0) continue;
                
                // 筛选该题型的题目
                List<Question> typeQuestions = questionList.stream()
                    .filter(q -> q.getType().equals(questionType))
                    .filter(q -> q.getStatus().equals(1)) // 只选择启用的题目
                    .collect(Collectors.toList());
                
                if (typeQuestions.isEmpty()) {
                    response.put("code", 400);
                    response.put("message", "题型 " + questionType + " 没有可用题目");
                    return ResponseEntity.ok(response);
                }
                
                // 随机选择题目
                Collections.shuffle(typeQuestions);
                int actualCount = Math.min(count, typeQuestions.size());
                List<Question> selectedTypeQuestions = typeQuestions.subList(0, actualCount);
                
                // 添加到选中题目集合
                selectedQuestions.addAll(selectedTypeQuestions);
                totalSelected += actualCount;
                
                // 计算总分
                for (Question question : selectedTypeQuestions) {
                    totalScore += question.getScore() != null ? question.getScore() : 10;
                }
            }
            
            // 如果选中的题目数量不足，补充随机题目
            if (totalSelected < totalQuestions) {
                // 筛选未被选中的启用题目
                List<Question> remainingQuestions = questionList.stream()
                    .filter(q -> q.getStatus().equals(1))
                    .filter(q -> !selectedQuestions.contains(q))
                    .collect(Collectors.toList());
                
                Collections.shuffle(remainingQuestions);
                int needMore = totalQuestions - totalSelected;
                int canAdd = Math.min(needMore, remainingQuestions.size());
                List<Question> additionalQuestions = remainingQuestions.subList(0, canAdd);
                
                selectedQuestions.addAll(additionalQuestions);
                totalSelected += canAdd;
                
                // 计算补充题目的分数
                for (Question question : additionalQuestions) {
                    totalScore += question.getScore() != null ? question.getScore() : 10;
                }
            }
            
            // 设置试卷的题目和总分
            generatedPaper.setQuestions(selectedQuestions);
            generatedPaper.setTotalScore(totalScore);
            
            // 保存生成的试卷
            ExamPaper savedPaper = examPaperRepository.save(generatedPaper);
            
            response.put("code", 200);
            response.put("message", "试卷生成成功");
            response.put("data", savedPaper);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "生成试卷失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}


