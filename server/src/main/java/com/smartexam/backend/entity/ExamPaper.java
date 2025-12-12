package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;

@Data
@Entity
@Table(name = "exam_paper")
public class ExamPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private Integer type;
    private Integer difficulty;
    private Integer totalQuestions;
    private Integer totalScore;
    private Integer duration;
    private Integer status;
    private String remark;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    @ManyToOne
    @JoinColumn(name = "create_by")
    private User createBy;
    
    @ManyToMany
    @JoinTable(name = "exam_paper_question",
            joinColumns = @JoinColumn(name = "paper_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    @JsonIgnore
    private Set<Question> questions;
    
    @OneToMany(mappedBy = "examPaper")
    @JsonIgnore
    private Set<ExamRecord> examRecords;
    
    private Long createTime;
    private Long updateTime;
}


