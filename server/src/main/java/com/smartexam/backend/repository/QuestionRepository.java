package com.smartexam.backend.repository;

import com.smartexam.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    
    // 根据科目ID查询题目列表
    List<Question> findBySubjectId(Long subjectId);
    
    // 根据题型查询题目列表
    List<Question> findByType(Integer type);
    
    // 根据难度查询题目列表
    List<Question> findByDifficulty(Integer difficulty);
    
    // 根据状态查询题目列表
    List<Question> findByStatus(Integer status);
    
    // 根据科目ID和题型查询题目列表
    List<Question> findBySubjectIdAndType(Long subjectId, Integer type);
    
    // 根据科目ID和难度查询题目列表
    List<Question> findBySubjectIdAndDifficulty(Long subjectId, Integer difficulty);
    
    // 根据科目ID和状态查询题目列表
    List<Question> findBySubjectIdAndStatus(Long subjectId, Integer status);
    
    // 根据创建者ID查询题目列表
    List<Question> findByCreateById(Long createById);
}


