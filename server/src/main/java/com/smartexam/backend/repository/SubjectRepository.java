package com.smartexam.backend.repository;

import com.smartexam.backend.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    // 根据状态查询科目列表
    List<Subject> findByStatus(Integer status);
    
    // 根据名称查询科目
    Subject findByName(String name);
    
    // 根据代码查询科目
    Subject findByCode(String code);
}