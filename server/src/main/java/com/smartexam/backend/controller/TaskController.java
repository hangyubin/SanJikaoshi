package com.smartexam.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartexam.backend.entity.ExamPaper;
import com.smartexam.backend.entity.Question;
import com.smartexam.backend.entity.Task;
import com.smartexam.backend.repository.ExamPaperRepository;
import com.smartexam.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ExamPaperRepository examPaperRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    // 获取所有任务
    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskRepository.findAll();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据ID获取任务
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (optionalTask.isPresent()) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", optionalTask.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "任务不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据创建者ID获取任务列表（使用分配者ID作为创建者ID）
    @GetMapping("/creator/{createById}")
    public ResponseEntity<?> getTasksByCreateById(@PathVariable Long createById) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskRepository.findByAssignById(createById);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据分配者ID获取任务列表
    @GetMapping("/assign/{assignById}")
    public ResponseEntity<?> getTasksByAssignById(@PathVariable Long assignById) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskRepository.findByAssignById(assignById);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据接收者ID获取任务列表
    @GetMapping("/receive/{receiveById}")
    public ResponseEntity<?> getTasksByReceiveById(@PathVariable Long receiveById) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskRepository.findByReceiveById(receiveById);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据状态获取任务列表
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTasksByStatus(@PathVariable Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskRepository.findByStatus(status);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据任务类型获取任务列表
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getTasksByType(@PathVariable Integer type) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Task> tasks = taskRepository.findByType(type);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 创建任务
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 创建任务对象
            Task task = new Task();
            
            // 设置基本信息
            task.setTitle((String) request.get("name"));
            task.setContent((String) request.get("description"));
            task.setType((Integer) request.get("type"));
            task.setStartTime((Long) request.get("startTime"));
            task.setEndTime((Long) request.get("endTime"));
            task.setDuration((Integer) request.get("duration"));
            task.setInstructions((String) request.get("instructions"));
            task.setStatus(1); // 默认待处理
            task.setCreateTime(System.currentTimeMillis());
            task.setUpdateTime(System.currentTimeMillis());
            
            // 获取试卷ID和题型分配比例
            Long paperId = ((Number) request.get("paperId")).longValue();
            Map<String, Integer> questionTypeDistribution = (Map<String, Integer>) request.get("questionTypeDistribution");
            Integer totalQuestions = (Integer) request.get("totalQuestions");
            if (totalQuestions == null) {
                totalQuestions = 50; // 默认50题
            }
            
            // 保存题型分配比例到任务
            task.setQuestionTypeDistribution(objectMapper.writeValueAsString(questionTypeDistribution));
            task.setTotalQuestions(totalQuestions);
            
            // 获取模板试卷
            Optional<ExamPaper> optionalPaper = examPaperRepository.findById(paperId);
            if (!optionalPaper.isPresent()) {
                response.put("code", 404);
                response.put("message", "题库不存在");
                return ResponseEntity.ok(response);
            }
            ExamPaper templatePaper = optionalPaper.get();
            
            // 创建新试卷
            ExamPaper generatedPaper = new ExamPaper();
            generatedPaper.setName(templatePaper.getName() + "_" + task.getTitle());
            generatedPaper.setDescription(templatePaper.getDescription());
            generatedPaper.setType(templatePaper.getType());
            generatedPaper.setDifficulty(templatePaper.getDifficulty());
            generatedPaper.setTotalQuestions(totalQuestions);
            generatedPaper.setDuration(task.getDuration());
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
            
            // 关联试卷到任务
            task.setExamPaper(savedPaper);
            
            // 保存任务
            Task savedTask = taskRepository.save(task);
            
            response.put("code", 200);
            response.put("message", "任务创建成功，试卷已生成");
            response.put("data", savedTask);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(response);
        }
    }

    // 更新任务
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (optionalTask.isPresent()) {
                Task existingTask = optionalTask.get();
                
                // 更新任务信息
                existingTask.setTitle(task.getTitle());
                existingTask.setContent(task.getContent());
                existingTask.setAssignBy(task.getAssignBy());
                existingTask.setAssignById(task.getAssignById());
                existingTask.setReceiveBy(task.getReceiveBy());
                existingTask.setReceiveById(task.getReceiveById());
                existingTask.setType(task.getType());
                existingTask.setStatus(task.getStatus());
                existingTask.setStartTime(task.getStartTime());
                existingTask.setEndTime(task.getEndTime());
                existingTask.setUpdateTime(System.currentTimeMillis());
                existingTask.setResult(task.getResult());
                
                Task updatedTask = taskRepository.save(existingTask);
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updatedTask);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "任务不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 删除任务
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (optionalTask.isPresent()) {
                taskRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "任务不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量删除任务
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteTasks(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            taskRepository.deleteAllById(ids);
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


