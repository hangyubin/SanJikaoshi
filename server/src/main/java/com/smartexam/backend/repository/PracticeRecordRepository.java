package com.smartexam.backend.repository;

import com.smartexam.backend.entity.PracticeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeRecordRepository extends JpaRepository<PracticeRecord, Long> {
    
    // 根据用户ID查询练习记录
    List<PracticeRecord> findByUserId(Long userId);
    
    // 根据用户ID和是否正确查询练习记录
    List<PracticeRecord> findByUserIdAndIsCorrect(Long userId, Boolean isCorrect);
    
    // 根据用户ID和题目ID查询练习记录
    List<PracticeRecord> findByUserIdAndQuestionId(Long userId, Long questionId);
    
    // 根据用户ID和科目ID查询练习记录
    List<PracticeRecord> findByUserIdAndSubjectId(Long userId, Integer subjectId);
    
    // 根据用户ID和题型查询练习记录
    List<PracticeRecord> findByUserIdAndType(Long userId, Integer type);
    
    // 根据用户ID和难度查询练习记录
    List<PracticeRecord> findByUserIdAndDifficulty(Long userId, Integer difficulty);
    
    // 根据用户ID和是否正确以及科目ID查询练习记录
    List<PracticeRecord> findByUserIdAndIsCorrectAndSubjectId(Long userId, Boolean isCorrect, Integer subjectId);
    
    // 根据用户ID和是否正确以及题型查询练习记录
    List<PracticeRecord> findByUserIdAndIsCorrectAndType(Long userId, Boolean isCorrect, Integer type);
    
    // 根据用户ID和是否正确以及难度查询练习记录
    List<PracticeRecord> findByUserIdAndIsCorrectAndDifficulty(Long userId, Boolean isCorrect, Integer difficulty);
}


