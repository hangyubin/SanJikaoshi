package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "exam_subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private String code;
    private Integer status;
    
    @OneToMany(mappedBy = "subject")
    private Set<Question> questions;
    
    @OneToMany(mappedBy = "subject")
    private Set<ExamPaper> examPapers;
    
    private Long createTime;
    private Long updateTime;
}


