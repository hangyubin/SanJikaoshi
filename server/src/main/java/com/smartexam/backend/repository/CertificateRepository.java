package com.smartexam.backend.repository;

import com.smartexam.backend.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    
    // 根据证书名称查询证书
    Certificate findByName(String name);
    
    // 根据科目ID查询证书列表
    java.util.List<Certificate> findBySubjectId(Long subjectId);
}