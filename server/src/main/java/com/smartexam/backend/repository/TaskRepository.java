package com.smartexam.backend.repository;

import com.smartexam.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // 根据创建者ID查询任务列表
    List<Task> findByCreateById(Long createById);
    
    // 根据分配者ID查询任务列表
    List<Task> findByAssignById(Long assignById);
    
    // 根据接收者ID查询任务列表
    List<Task> findByReceiveById(Long receiveById);
    
    // 根据状态查询任务列表
    List<Task> findByStatus(Integer status);
    
    // 根据任务类型查询任务列表
    List<Task> findByType(Integer type);
    
    // 根据创建者ID和状态查询任务列表
    List<Task> findByCreateByIdAndStatus(Long createById, Integer status);
    
    // 根据接收者ID和状态查询任务列表
    List<Task> findByReceiveByIdAndStatus(Long receiveById, Integer status);
    
    // 根据接收者ID和任务类型查询任务列表
    List<Task> findByReceiveByIdAndType(Long receiveById, Integer type);
}