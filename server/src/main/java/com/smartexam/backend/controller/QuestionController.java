package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Question;
import com.smartexam.backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    // 获取所有题目（支持分页、搜索、筛选）
    @GetMapping
    public ResponseEntity<?> getAllQuestions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 创建排序对象
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            Sort sort = Sort.by(direction, sortBy);
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
            
            // 创建动态查询条件
            Specification<Question> specification = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 题目内容搜索
                if (content != null && !content.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("content"), "%" + content + "%"));
                }
                
                // 科目筛选
                if (subjectId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("subject").get("id"), subjectId));
                }
                
                // 题型筛选
                if (type != null) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }
                
                // 难度筛选
                if (difficulty != null) {
                    predicates.add(criteriaBuilder.equal(root.get("difficulty"), difficulty));
                }
                
                // 组合查询条件
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            
            // 执行分页查询
            Page<Question> questionPage = questionRepository.findAll(specification, pageable);
            
            // 构建分页结果
            Map<String, Object> pageResult = new HashMap<>();
            pageResult.put("records", questionPage.getContent());
            pageResult.put("total", questionPage.getTotalElements());
            pageResult.put("page", page);
            pageResult.put("pageSize", pageSize);
            pageResult.put("pages", questionPage.getTotalPages());
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", pageResult);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据ID获取题目
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Question> optionalQuestion = questionRepository.findById(id);
            if (optionalQuestion.isPresent()) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", optionalQuestion.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "题目不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据科目ID获取题目列表
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<?> getQuestionsBySubjectId(@PathVariable Long subjectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Question> questions = questionRepository.findBySubjectId(subjectId);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", questions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据题型获取题目列表
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getQuestionsByType(@PathVariable Integer type) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Question> questions = questionRepository.findByType(type);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", questions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据难度获取题目列表
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<?> getQuestionsByDifficulty(@PathVariable Integer difficulty) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Question> questions = questionRepository.findByDifficulty(difficulty);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", questions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据状态获取题目列表
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getQuestionsByStatus(@PathVariable Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Question> questions = questionRepository.findByStatus(status);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", questions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据创建者ID获取题目列表
    @GetMapping("/creator/{createById}")
    public ResponseEntity<?> getQuestionsByCreateById(@PathVariable Long createById) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Question> questions = questionRepository.findByCreateById(createById);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", questions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 创建题目
    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody Question question) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置默认值
            if (question.getCreateTime() == null) {
                question.setCreateTime(System.currentTimeMillis());
            }
            if (question.getUpdateTime() == null) {
                question.setUpdateTime(System.currentTimeMillis());
            }
            if (question.getStatus() == null) {
                question.setStatus(1); // 默认启用
            }
            if (question.getScore() == null) {
                question.setScore(10); // 默认10分
            }
            
            Question savedQuestion = questionRepository.save(question);
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", savedQuestion);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 更新题目
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Question> optionalQuestion = questionRepository.findById(id);
            if (optionalQuestion.isPresent()) {
                Question existingQuestion = optionalQuestion.get();
                
                // 更新题目信息
                existingQuestion.setContent(question.getContent());
                existingQuestion.setOptions(question.getOptions());
                existingQuestion.setAnswer(question.getAnswer());
                existingQuestion.setAnalysis(question.getAnalysis());
                existingQuestion.setSubject(question.getSubject());
                existingQuestion.setType(question.getType());
                existingQuestion.setDifficulty(question.getDifficulty());
                existingQuestion.setScore(question.getScore());
                existingQuestion.setStatus(question.getStatus());
                existingQuestion.setCreateBy(question.getCreateBy());
                existingQuestion.setUpdateTime(System.currentTimeMillis());
                
                Question updatedQuestion = questionRepository.save(existingQuestion);
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updatedQuestion);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "题目不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 删除题目
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Question> optionalQuestion = questionRepository.findById(id);
            if (optionalQuestion.isPresent()) {
                questionRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "题目不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量删除题目
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteQuestions(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            questionRepository.deleteAllById(ids);
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


