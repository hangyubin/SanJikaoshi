package com.smartexam.backend.repository;

import com.smartexam.backend.entity.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {
    
    // 根据科目ID查询试卷列表
    List<ExamPaper> findBySubjectId(Long subjectId);
    
    // 根据创建者ID查询试卷列表
    List<ExamPaper> findByCreateById(Long createById);
    
    // 根据状态查询试卷列表
    List<ExamPaper> findByStatus(Integer status);
    
    // 根据科目ID和状态查询试卷列表
    List<ExamPaper> findBySubjectIdAndStatus(Long subjectId, Integer status);
}