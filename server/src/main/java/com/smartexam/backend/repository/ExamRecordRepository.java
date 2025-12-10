package com.smartexam.backend.repository;

import com.smartexam.backend.entity.ExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRecordRepository extends JpaRepository<ExamRecord, Long> {
    
    // 根据用户ID查询考试记录列表
    List<ExamRecord> findByUserId(Long userId);
    
    // 根据试卷ID查询考试记录列表
    List<ExamRecord> findByExamPaperId(Long examPaperId);
    
    // 根据用户ID和试卷ID查询考试记录
    List<ExamRecord> findByUserIdAndExamPaperId(Long userId, Long examPaperId);
    
    // 根据状态查询考试记录列表
    List<ExamRecord> findByStatus(Integer status);
    
    // 根据用户ID和状态查询考试记录列表
    List<ExamRecord> findByUserIdAndStatus(Long userId, Integer status);
    
    // 根据用户ID和得分范围查询考试记录列表
    List<ExamRecord> findByUserIdAndScoreBetween(Long userId, Integer minScore, Integer maxScore);
    
    // 根据用户ID和考试时间范围查询考试记录列表
    List<ExamRecord> findByUserIdAndExamTimeBetween(Long userId, Long startTime, Long endTime);
}


