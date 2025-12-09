package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "sys_user_certificate")
public class UserCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;
    
    private Integer score;
    private Long issueDate;
    private Long expireDate;
    private Integer status;
    private String certificateNumber;
    
    private Long createTime;
    private Long updateTime;
}