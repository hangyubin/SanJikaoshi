package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Set<Question> questions;
    
    @OneToMany(mappedBy = "subject")
    @JsonIgnore
    private Set<ExamPaper> examPapers;
    
    private Long createTime;
    private Long updateTime;
}


