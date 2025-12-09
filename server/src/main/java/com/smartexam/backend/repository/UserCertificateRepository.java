package com.smartexam.backend.repository;

import com.smartexam.backend.entity.UserCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCertificateRepository extends JpaRepository<UserCertificate, Long> {
    
    // 根据用户ID查询证书列表
    List<UserCertificate> findByUserId(Long userId);
    
    // 根据证书ID查询用户证书列表
    List<UserCertificate> findByCertificateId(Long certificateId);
    
    // 根据用户ID和证书ID查询用户证书
    UserCertificate findByUserIdAndCertificateId(Long userId, Long certificateId);
    
    // 根据用户和证书查询用户证书
    UserCertificate findByUserAndCertificate(com.smartexam.backend.entity.User user, com.smartexam.backend.entity.Certificate certificate);
}