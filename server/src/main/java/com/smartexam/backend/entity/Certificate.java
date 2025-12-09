package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "exam_certificate")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private Integer passScore;
    private Integer validityPeriod;
    private Integer status;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    private Long createTime;
    private Long updateTime;
}