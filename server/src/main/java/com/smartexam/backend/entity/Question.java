package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "exam_question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    private Integer type; // 1:选择题 2:判断题 3:填空题 4:简答题
    private String answer;
    private String options;
    private Integer score;
    private Integer difficulty; // 1:简单 2:中等 3:困难
    private String analysis; // 解析
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    @ManyToOne
    @JoinColumn(name = "create_by")
    private User createBy;
    
    @ManyToMany(mappedBy = "questions")
    private Set<ExamPaper> examPapers;
    
    private Integer status;
    private Long createTime;
    private Long updateTime;
}


