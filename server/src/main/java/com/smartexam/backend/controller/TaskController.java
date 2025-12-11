package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Task;
import com.smartexam.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

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
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置默认值
            if (task.getCreateTime() == null) {
                task.setCreateTime(System.currentTimeMillis());
            }
            if (task.getUpdateTime() == null) {
                task.setUpdateTime(System.currentTimeMillis());
            }
            if (task.getStatus() == null) {
                task.setStatus(1); // 默认待处理
            }
            if (task.getType() == null) {
                task.setType(1); // 默认普通任务
            }
            
            Task savedTask = taskRepository.save(task);
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", savedTask);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
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


