package com.smartexam.backend.repository;

import com.smartexam.backend.entity.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {
    
    // 根据科目ID查询学习资源列表
    List<LearningResource> findBySubjectId(Long subjectId);
    
    // 根据资源类型查询学习资源列表
    List<LearningResource> findByType(Integer type);
    
    // 根据创建者ID查询学习资源列表
    List<LearningResource> findByCreateById(Long createById);
}