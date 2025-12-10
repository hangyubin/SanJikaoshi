package com.smartexam.backend.repository;

import com.smartexam.backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // 根据科室编码查询科室
    Department findByCode(String code);
    
    // 根据科室名称查询科室
    Department findByName(String name);
}


